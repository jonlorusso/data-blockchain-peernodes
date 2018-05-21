package com.swatt.blockchain.repository;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.swatt.blockchain.entity.Entity;
import com.swatt.util.general.OperationFailedException;
import com.swatt.util.general.StringUtilities;
import com.swatt.util.sql.ConnectionPool;

public class CrudRepository<T extends Entity> extends Repository<T> {
    
    private final static int INITIAL_SIZE = 100;
    
    private Class<T> type;
    
    private String primaryKey;
    private Field primaryKeyField;
    
    private List<String> columns = new ArrayList<>();
    private List<Field> columnFields = new ArrayList<>();

    private Map<Field, Method> columnSetMethods = new HashMap<>();
    private Map<Field, Method> columnGetMethods = new HashMap<>();
    
    private String insertQuery;
    private String updateQuery;

    private ConnectionPool connectionPool;
    
    @SuppressWarnings("unchecked")
    public CrudRepository(ConnectionPool connectionPool) {
        Type type = getClass().getGenericSuperclass();
        
        while (!(type instanceof ParameterizedType) || ((ParameterizedType) type).getRawType() != CrudRepository.class) {
            if (type instanceof ParameterizedType) {
                type = ((Class<?>) ((ParameterizedType) type).getRawType()).getGenericSuperclass();
            } else {
                type = ((Class<?>) type).getGenericSuperclass();
            }
        }
        
        this.type = (Class<T>)((ParameterizedType)type).getActualTypeArguments()[0];
        
        try {
            for (Field field : this.type.getDeclaredFields()) {
                if (field.getDeclaredAnnotation(Column.class) != null) {
                    this.columnFields.add(field);
                    this.columns.add(columnForField(field));

                    Method getMethod;
                    if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                        getMethod = this.type.getMethod("is" + LOWER_CAMEL.to(UPPER_CAMEL, field.getName()));
                    } else {
                        getMethod = this.type.getMethod("get" + LOWER_CAMEL.to(UPPER_CAMEL, field.getName()));
                    }
                    columnGetMethods.put(field, getMethod);
                    
                    Method setMethod = this.type.getMethod("set" + LOWER_CAMEL.to(UPPER_CAMEL, field.getName()), field.getType());
                    columnSetMethods.put(field, setMethod);
                }

                // TODO support combination keys
                if (field.getDeclaredAnnotation(Id.class) != null) {
                    this.primaryKeyField = field;
                    this.primaryKey = columnForField(field);
                }
            }
        } catch (NoSuchMethodException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        this.connectionPool = connectionPool;
    }
    
    private String getTableName() {
        return UPPER_CAMEL.to(UPPER_UNDERSCORE, type.getSimpleName());
    }
    
    public T forResultSet(ResultSet resultSet) throws OperationFailedException {
        try {
            T t = this.type.newInstance();
            int parameterIndex = 1;
            for (Field field : columnFields) {
                Object value = resultSet.getObject(parameterIndex++);
                if (value != null) {
                    columnSetMethods.get(field).invoke(t, value); //resultSet.getObject(parameterIndex++));
                }
            }
            return t;
        } catch (NullPointerException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SQLException e) {
            throw new OperationFailedException(e);
        }
    }
    
    private String buildUpdateQuery() {
        String valuesString = String.join(", ", columns.stream().map(s -> toBindableValue(s)).collect(Collectors.toList()));
        return String.format("UPDATE %s SET %s WHERE %s", getTableName(), valuesString, buildPrimaryKeyWhereClause());
    }
    
    public T update(T t) throws SQLException, OperationFailedException {
        if (updateQuery == null)
            updateQuery = buildUpdateQuery();
        
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                int parameterIndex = applyToPreparedStatement(1, preparedStatement, t, true);
                preparedStatement.setObject(parameterIndex, getPrimaryKeyValue(t));
                preparedStatement.executeUpdate();
            }
        }

        return t;
    }
    
    private int applyToPreparedStatement(int parameterIndex, PreparedStatement preparedStatement, T t, boolean includePrimaryKey) throws OperationFailedException {
        try {
            for (Field field : columnFields) {
                if (!field.equals(primaryKeyField) || includePrimaryKey)
                    preparedStatement.setObject(parameterIndex++, columnGetMethods.get(field).invoke(t));
            }
        } catch (SQLException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new OperationFailedException(e);
        }
        
        return parameterIndex;
    }
    
    private Object getPrimaryKeyValue(T t) throws OperationFailedException {
        try {
            return columnGetMethods.get(primaryKeyField).invoke(t);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new OperationFailedException(e);
        }
    }

    private String columnForField(Field field) {
        Column columnAnnotation = field.getDeclaredAnnotation(Column.class);
        String columnName = columnAnnotation.columnDefinition();
        
        if (StringUtilities.isNullOrAllWhiteSpace(columnName))
            columnName = LOWER_CAMEL.to(UPPER_UNDERSCORE, field.getName());
        
        return columnName;
    }
    
    public T find(Object primaryKey) throws SQLException, OperationFailedException {
        String selectQuery = buildSelectQueryWithWhereClause(buildPrimaryKeyWhereClause());
        
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
                ps.setObject(1, primaryKey);
                
                try (ResultSet resultSet = ps.executeQuery()) {
                    return forResultSet(resultSet);
                }
            }
        }
    }
    
    public T findBy(Map<String, Object> parameters) throws OperationFailedException, SQLException {
        List<String> keys = parameters.keySet().stream().collect(Collectors.toList());
        String whereClause = String.join(" AND ", keys.stream().map(k -> toBindableValue(k)).collect(Collectors.toList()));
        String selectQuery = buildSelectQueryWithWhereClause(whereClause);
        
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
                int parameterIndex = 1;
                for (String key : keys) {
                    ps.setObject(parameterIndex++, parameters.get(key));
                }
                try (ResultSet resultSet = ps.executeQuery()) {
                    return resultSet.next() ? forResultSet(resultSet) : null;
                }
            }
        }
    }

    public List<T> findAll() throws OperationFailedException, SQLException {
        return findAllBy(null);
    }
    
    private Map<String, String> findAllByQueryCache = new HashMap<>();
    
    public List<T> findAllBy(Map<String, Object> parameters) throws OperationFailedException, SQLException {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String caller = stackTraceElements[2].getMethodName();
        
        List<String> keys = null;
        String selectQuery;
        
        if (parameters != null) {
            keys = parameters.keySet().stream().collect(Collectors.toList());
            
            if (findAllByQueryCache.get(caller) != null) {
                selectQuery = findAllByQueryCache.get(caller);
            } else {
                String whereClause = String.join(" AND ", keys.stream().map(k -> toBindableValue(k)).collect(Collectors.toList()));
                selectQuery = buildSelectQueryWithWhereClause(whereClause);
                findAllByQueryCache.put(caller, selectQuery);
            }
        } else {
            selectQuery = buildSelectQuery();
        }
        
        List<T> result = new ArrayList<>(INITIAL_SIZE);
        
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
                if (parameters != null) {
                    int parameterIndex = 1;
                    for (String key : keys) {
                        ps.setObject(parameterIndex++, parameters.get(key));
                    }
                }
                
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) 
                        result.add(forResultSet(resultSet));
                }
            }
        }
        
        return result;
    }
    
    private String buildInsertQuery() {
        List<String> insertColumns = columns.stream().filter(s -> !s.equals(primaryKey)).collect(Collectors.toList());
        String valuesString = String.join(",", insertColumns.stream().map(s -> "?").collect(Collectors.toList()));
        return String.format("INSERT INTO %s (%s) VALUES (%s)", getTableName(), String.join(", ", insertColumns), valuesString);
    }
    
    public T insert(T t) throws OperationFailedException, SQLException {
        if (insertQuery == null)
            insertQuery = buildInsertQuery();
        
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                applyToPreparedStatement(1, preparedStatement, t, false);
                preparedStatement.executeUpdate();
                
                if (primaryKeyField.getAnnotation(GeneratedValue.class) != null) {
                    try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                        if (resultSet.next()) {
                            int autoGeneratedKey = resultSet.getInt(1);
                            columnSetMethods.get(primaryKeyField).invoke(t, autoGeneratedKey);
                        }
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                throw new OperationFailedException(e);
            }
        }
        
        return t;
    }
    
    private String toBindableValue(String column) {
        return String.format("%s = ?", column);
    }
    
    private String buildPrimaryKeyWhereClause() {
        return toBindableValue(primaryKey);
    }
    
    private String buildSelectQueryWithWhereClause(String whereClause) {
        return String.format("%s WHERE %s", buildSelectQuery(), whereClause);
    }
    
    private String buildSelectQuery() {
        return String.format("SELECT %s FROM %s", String.join(", ", columns), getTableName());
    }
    
    public void deleteAll() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("TRUNCATE TABLE " + getTableName())) {
                ps.executeUpdate();
            }
        }
    }
    
    private String buildDeleteQuery(String whereClause) {
        return String.format("DELETE FROM %s WHERE %s", getTableName(), whereClause);
    }
    
    public int delete(Map<String, Object> parameters) throws OperationFailedException, SQLException {
        List<String> keys = parameters.keySet().stream().collect(Collectors.toList());
        String whereClause = String.join(" AND ", keys.stream().map(k -> toBindableValue(k)).collect(Collectors.toList()));
        String deleteQuery = buildDeleteQuery(whereClause);

        try (Connection connection = connectionPool.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
                return ps.executeUpdate();
            }
        }
    }
}
