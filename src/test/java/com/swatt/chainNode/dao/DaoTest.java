package com.swatt.chainNode.dao;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.mockito.Mock;

import com.google.common.base.CaseFormat;

public class DaoTest {

    private Class<?> classUnderTest;

    @Mock
    private Connection connection;
    
    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;
    
    public DaoTest(Class<?> clazz) {
        this.classUnderTest = clazz;
    }

    private Object createInstance() throws Exception {
        Object instance = classUnderTest.newInstance();

        Arrays.stream(classUnderTest.getDeclaredFields()).forEach(f -> {
            try {
                String setMethodName = format("set%s", LOWER_CAMEL.to(UPPER_CAMEL, f.getName()));
                Method setMethod = classUnderTest.getDeclaredMethod(setMethodName, f.getType());

                String getMethodName = format("get%s", LOWER_CAMEL.to(UPPER_CAMEL, f.getName()));
                Method getMethod = classUnderTest.getDeclaredMethod(getMethodName);
                
                Method setScaleMethod = null;
                String setScaleMethodName = format("set%sScale", LOWER_CAMEL.to(UPPER_CAMEL, f.getName()));
                try {
                    setScaleMethod = classUnderTest.getDeclaredMethod(setScaleMethodName, int.class);
                } catch (NoSuchMethodException e) {
                }

                if (f.getType() == String.class) {
                    setMethod.invoke(instance, f.getName());
                    assertThat(getMethod.invoke(instance), is(f.getName()));
                }

                if (f.getType() == int.class) {
                    int x = (int)System.currentTimeMillis();
                    setMethod.invoke(instance, x);
                    assertThat(getMethod.invoke(instance), is(x));
                }

                if (f.getType() == boolean.class) {
                    boolean b = System.currentTimeMillis() % 2 == 0;
                    setMethod.invoke(instance, b);
                    assertThat(getMethod.invoke(instance), is(b));
                }

                if (f.getType() == long.class) {
                    long x = System.currentTimeMillis();
                    setMethod.invoke(instance, x);
                    
                    if (setScaleMethod != null) {
                        setScaleMethod.invoke(instance, 0);
                        
                        double result = (double)getMethod.invoke(instance);
                        assertThat(Double.valueOf(result).longValue(), is(x));
                    } else {
                        assertThat(getMethod.invoke(instance), is(x));
                    }
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            }
        });
        
        return instance;
    }
    
    public void testConstructorGettersAndSetters() throws Exception {
        try {
            createInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testInsert() throws Exception {
        Object instance = createInstance();
        
        String primaryKey = classUnderTest.getDeclaredFields()[0].getName();
        
        String insertMethodName = format("insert%s", classUnderTest.getSimpleName()); 
        Method insertMethod = classUnderTest.getDeclaredMethod(insertMethodName, Connection.class, classUnderTest);

        Method columnListMethod = classUnderTest.getDeclaredMethod("getSqlColumnList");
        String columnList = (String)columnListMethod.invoke(classUnderTest);
        String[] columns = columnList.split(", ");
        
        if (primaryKey.equals("id")) {
            columns = Arrays.copyOfRange(columns, 1, columns.length);
            columnList = String.join(", ", Arrays.stream(columns).collect(Collectors.toList()));
        }
        
        String parameters = String.join(", ", Arrays.stream(columns).map(c -> "?").collect(Collectors.toList()));
        String tableName = UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, classUnderTest.getSimpleName());
        
        String insertSql = format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnList, parameters);
        when(connection.prepareStatement(insertSql)).thenReturn(preparedStatement);
        
        String maxIdSql = String.format("Select MAX(%s) FROM %s", primaryKey.toUpperCase(), tableName);
        when(connection.prepareStatement(maxIdSql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        
        insertMethod.invoke(classUnderTest, connection, instance);
    }
}
