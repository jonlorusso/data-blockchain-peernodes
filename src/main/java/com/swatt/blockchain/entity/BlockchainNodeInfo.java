package com.swatt.blockchain.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.swatt.util.io.DataStreamSerializable;
import com.swatt.util.io.DataStreamUtilities;
import com.swatt.util.io.SerializationException;

public class BlockchainNodeInfo implements DataStreamSerializable {
    private String code;
    private String forkCode;
    private String name;
    private String description;
    private String units;
    private String txFeeUnits;
    private String ip;
    private int port;
    private int forwardedPort;
    private String rpcUn;
    private String rpcPw;
    private String className;
    private int difficultyScaling;
    private int rewardScaling;
    private int feeScaling;
    private int amountScaling;
    private int zmqPort;
    private boolean enabled;

    public BlockchainNodeInfo() {
    }

    public BlockchainNodeInfo(String code, String forkCode, String name, String description, String units,
            String txFeeUnits, String ip, int port, int forwardedPort, String rpcUn, String rpcPw, String className,
            int difficultyScaling, int rewardScaling, int feeScaling, int amountScaling, int zmqPort, boolean enabled) {
        this.code = code;
        this.forkCode = forkCode;
        this.name = name;
        this.description = description;
        this.units = units;
        this.txFeeUnits = txFeeUnits;
        this.ip = ip;
        this.port = port;
        this.forwardedPort = forwardedPort;
        this.rpcUn = rpcUn;
        this.rpcPw = rpcPw;
        this.className = className;
        this.difficultyScaling = difficultyScaling;
        this.rewardScaling = rewardScaling;
        this.feeScaling = feeScaling;
        this.amountScaling = amountScaling;
        this.zmqPort = zmqPort;
        this.enabled = enabled;
    }

    public final String getCode() {
        return code;
    }

    public final String getForkCode() {
        return forkCode;
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final String getUnits() {
        return units;
    }

    public final String getTxFeeUnits() {
        return txFeeUnits;
    }

    public final String getIp() {
        return ip;
    }

    public final int getPort() {
        return port;
    }

    public final int getForwardedPort() {
        return forwardedPort;
    }

    public final String getRpcUn() {
        return rpcUn;
    }

    public final String getRpcPw() {
        return rpcPw;
    }

    public final String getClassName() {
        return className;
    }

    public final int getDifficultyScaling() {
        return difficultyScaling;
    }

    public final int getRewardScaling() {
        return rewardScaling;
    }

    public final int getFeeScaling() {
        return feeScaling;
    }

    public final int getAmountScaling() {
        return amountScaling;
    }

    public int getZmqPort() {
        return zmqPort;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public final void setCode(String code) {
        this.code = code;
    }

    public final void setForkCode(String forkCode) {
        this.forkCode = forkCode;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final void setUnits(String units) {
        this.units = units;
    }

    public final void setTxFeeUnits(String txFeeUnits) {
        this.txFeeUnits = txFeeUnits;
    }

    public final void setIp(String ip) {
        this.ip = ip;
    }

    public final void setPort(int port) {
        this.port = port;
    }

    public final void setForwardedPort(int forwardedPort) {
        this.forwardedPort = forwardedPort;
    }

    public final void setRpcUn(String rpcUn) {
        this.rpcUn = rpcUn;
    }

    public final void setRpcPw(String rpcPw) {
        this.rpcPw = rpcPw;
    }

    public final void setClassName(String className) {
        this.className = className;
    }

    public final void setDifficultyScaling(int difficultyScaling) {
        this.difficultyScaling = difficultyScaling;
    }

    public final void setRewardScaling(int rewardScaling) {
        this.rewardScaling = rewardScaling;
    }

    public final void setFeeScaling(int feeScaling) {
        this.feeScaling = feeScaling;
    }

    public final void setAmountScaling(int amountScaling) {
        this.amountScaling = amountScaling;
    }

    public void setZmqPort(int zmqPort) {
        this.zmqPort = zmqPort;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static String getSqlColumnList() {
        return "CODE, FORK_CODE, NAME, DESCRIPTION, UNITS, TX_FEE_UNITS, IP, PORT, FORWARDED_PORT, RPC_UN, RPC_PW, CLASS_NAME, DIFFICULTY_SCALING, REWARD_SCALING, FEE_SCALING, AMOUNT_SCALING, ZMQ_PORT, ENABLED";
    }

    private final static String primaryKeySelect = "SELECT CODE, FORK_CODE, NAME, DESCRIPTION, UNITS, TX_FEE_UNITS, IP, PORT, FORWARDED_PORT, RPC_UN, RPC_PW, CLASS_NAME, DIFFICULTY_SCALING, REWARD_SCALING, FEE_SCALING, AMOUNT_SCALING, ZMQ_PORT, ENABLED FROM BLOCKCHAIN_NODE_INFO WHERE CODE = ?";
    
    public static String getStandardTableName() {
        return "BLOCKCHAIN_NODE_INFO";
    }

    public BlockchainNodeInfo(ResultSet rs) throws SQLException {
        code = rs.getString(1);
        forkCode = rs.getString(2);
        name = rs.getString(3);
        description = rs.getString(4);
        units = rs.getString(5);
        txFeeUnits = rs.getString(6);
        ip = rs.getString(7);
        port = rs.getInt(8);
        forwardedPort = rs.getInt(9);
        rpcUn = rs.getString(10);
        rpcPw = rs.getString(11);
        className = rs.getString(12);
        difficultyScaling = rs.getInt(13);
        rewardScaling = rs.getInt(14);
        feeScaling = rs.getInt(15);
        amountScaling = rs.getInt(16);
        zmqPort = rs.getInt(17);
        enabled = rs.getBoolean(18);
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();

        return getBlockchainNodeInfos(rs);
    }

    public static BlockchainNodeInfo getNextBlockchainNodeInfo(ResultSet rs) throws SQLException {
        if (rs.next())
            return new BlockchainNodeInfo(rs);

        else
            return null;
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(ResultSet rs) throws SQLException {
        ArrayList<BlockchainNodeInfo> results = new ArrayList<BlockchainNodeInfo>(100);

        while (rs.next())
            results.add(new BlockchainNodeInfo(rs));

        return results;
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(ResultSet rs, int max) throws SQLException {
        ArrayList<BlockchainNodeInfo> results = new ArrayList<BlockchainNodeInfo>(100);

        for (int i = 0; (i < max) && rs.next(); i++)
            results.add(new BlockchainNodeInfo(rs));

        return results;
    }

    private static String SELECT_ALL_QUERY = "SELECT " + getSqlColumnList() + " FROM " + getStandardTableName();

    public static ArrayList<BlockchainNodeInfo> getAllBlockchainNodeInfos(Connection connection) throws SQLException {
        return getBlockchainNodeInfos(connection, null);
    }

    public static ArrayList<BlockchainNodeInfo> getAllBlockchainNodeInfos(Connection connection, int max)
            throws SQLException {
        return getBlockchainNodeInfos(connection, null, max);
    }

    // TODO close prepared statements elsewhere
    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(Connection connection, String where, Object...params) throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            int parameterIndex = 1;
            for (Object param : params) {
                ps.setObject(parameterIndex++, param);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return getBlockchainNodeInfos(rs);
            }
        }
    }
    
    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(Connection connection, String where, int max)
            throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        return getBlockchainNodeInfos(rs, max);
    }

    public static BlockchainNodeInfo getFirstBlockchainNodeInfo(Connection connection, String where)
            throws SQLException {
        String query = SELECT_ALL_QUERY;

        if (where != null)
            query += " WHERE " + where;

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        return getNextBlockchainNodeInfo(rs);
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(DataSource dataSource, String where)
            throws SQLException {
        Connection connection = dataSource.getConnection();
        ArrayList<BlockchainNodeInfo> results = getBlockchainNodeInfos(connection, where);
        connection.close();
        return results;
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(String jndiName, String where)
            throws SQLException, NamingException {
        InitialContext ctx = new InitialContext();
        DataSource dataSource = (DataSource) ctx.lookup(jndiName);
        return getBlockchainNodeInfos(dataSource, where);
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(String jndiName)
            throws SQLException, NamingException {
        return getBlockchainNodeInfos(jndiName, null);
    }

    public static ArrayList<BlockchainNodeInfo> getBlockchainNodeInfos(DataSource dataSource) throws SQLException {
        return getBlockchainNodeInfos(dataSource, null);
    }

    public static BlockchainNodeInfo insertBlockchainNodeInfo(Connection connection, String code, String forkCode,
            String name, String description, String units, String txFeeUnits, String ip, int port, int forwardedPort,
            String rpcUn, String rpcPw, String className, int difficultyScaling, int rewardScaling, int feeScaling,
            int amountScaling, int zmqPort, boolean enabled) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO BLOCKCHAIN_NODE_INFO (CODE, FORK_CODE, NAME, DESCRIPTION, UNITS, TX_FEE_UNITS, IP, PORT, FORWARDED_PORT, RPC_UN, RPC_PW, CLASS_NAME, DIFFICULTY_SCALING, REWARD_SCALING, FEE_SCALING, AMOUNT_SCALING, ZMQ_PORT, ENABLED) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ps.setString(1, code);
        ps.setString(2, forkCode);
        ps.setString(3, name);
        ps.setString(4, description);
        ps.setString(5, units);
        ps.setString(6, txFeeUnits);
        ps.setString(7, ip);
        ps.setInt(8, port);
        ps.setInt(9, forwardedPort);
        ps.setString(10, rpcUn);
        ps.setString(11, rpcPw);
        ps.setString(12, className);
        ps.setInt(13, difficultyScaling);
        ps.setInt(14, rewardScaling);
        ps.setInt(15, feeScaling);
        ps.setInt(16, amountScaling);
        ps.setInt(17,  zmqPort);
        ps.setBoolean(18, enabled);
        ps.executeUpdate();

        int autoGeneratedKey = 0;

        ps = connection.prepareStatement("Select MAX(CODE) FROM BLOCKCHAIN_NODE_INFO");
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            autoGeneratedKey = rs.getInt(1);

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        return new BlockchainNodeInfo(code, forkCode, name, description, units, txFeeUnits, ip, port, forwardedPort,
                rpcUn, rpcPw, className, difficultyScaling, rewardScaling, feeScaling, amountScaling, zmqPort, enabled);
    }

    public static BlockchainNodeInfo updateBlockchainNodeInfo(Connection connection, String code, String forkCode,
            String name, String description, String units, String txFeeUnits, String ip, int port, int forwardedPort,
            String rpcUn, String rpcPw, String className, int difficultyScaling, int rewardScaling, int feeScaling,
            int amountScaling, int zmqPort, boolean enabled) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE BLOCKCHAIN_NODE_INFO SET FORK_CODE = ?, NAME = ?, DESCRIPTION = ?, UNITS = ?, TX_FEE_UNITS = ?, IP = ?, PORT = ?, FORWARDED_PORT = ?, RPC_UN = ?, RPC_PW = ?, CLASS_NAME = ?, DIFFICULTY_SCALING = ?, REWARD_SCALING = ?, FEE_SCALING = ?, AMOUNT_SCALING = ?, ZMQ_PORT = ?, ENABLED = ? WHERE CODE = ?");

        ps.setString(1, forkCode);
        ps.setString(2, name);
        ps.setString(3, description);
        ps.setString(4, units);
        ps.setString(5, txFeeUnits);
        ps.setString(6, ip);
        ps.setInt(7, port);
        ps.setInt(8, forwardedPort);
        ps.setString(9, rpcUn);
        ps.setString(10, rpcPw);
        ps.setString(11, className);
        ps.setInt(12, difficultyScaling);
        ps.setInt(13, rewardScaling);
        ps.setInt(14, feeScaling);
        ps.setInt(15, amountScaling);
        ps.setInt(16, zmqPort);
        ps.setBoolean(17, enabled);
        ps.setString(18, code);
        ps.executeUpdate();

        return new BlockchainNodeInfo(code, forkCode, name, description, units, txFeeUnits, ip, port, forwardedPort, rpcUn, rpcPw, className, difficultyScaling, rewardScaling, feeScaling, amountScaling, zmqPort, enabled);
    }

    public static BlockchainNodeInfo insertBlockchainNodeInfo(Connection connection, BlockchainNodeInfo blockchainNodeInfo) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO BLOCKCHAIN_NODE_INFO (CODE, FORK_CODE, NAME, DESCRIPTION, UNITS, TX_FEE_UNITS, IP, PORT, FORWARDED_PORT, RPC_UN, RPC_PW, CLASS_NAME, DIFFICULTY_SCALING, REWARD_SCALING, FEE_SCALING, AMOUNT_SCALING, ZMQ_PORT, ENABLED) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ps.setString(1, blockchainNodeInfo.code);
        ps.setString(2, blockchainNodeInfo.forkCode);
        ps.setString(3, blockchainNodeInfo.name);
        ps.setString(4, blockchainNodeInfo.description);
        ps.setString(5, blockchainNodeInfo.units);
        ps.setString(6, blockchainNodeInfo.txFeeUnits);
        ps.setString(7, blockchainNodeInfo.ip);
        ps.setInt(8, blockchainNodeInfo.port);
        ps.setInt(9, blockchainNodeInfo.forwardedPort);
        ps.setString(10, blockchainNodeInfo.rpcUn);
        ps.setString(11, blockchainNodeInfo.rpcPw);
        ps.setString(12, blockchainNodeInfo.className);
        ps.setInt(13, blockchainNodeInfo.difficultyScaling);
        ps.setInt(14, blockchainNodeInfo.rewardScaling);
        ps.setInt(15, blockchainNodeInfo.feeScaling);
        ps.setInt(16, blockchainNodeInfo.amountScaling);
        ps.setInt(17, blockchainNodeInfo.zmqPort);
        ps.setBoolean(18, blockchainNodeInfo.enabled);
        ps.executeUpdate();

        ps = connection.prepareStatement("Select MAX(CODE) FROM BLOCKCHAIN_NODE_INFO");
        ResultSet rs = ps.executeQuery();

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        return blockchainNodeInfo;
    }

    public static BlockchainNodeInfo replaceBlockchainNodeInfo(Connection connection, BlockchainNodeInfo blockchainNodeInfo) throws SQLException {
        boolean storedAutoCommitValue = connection.getAutoCommit();

        if (storedAutoCommitValue)
            connection.setAutoCommit(false);

        PreparedStatement ps = connection.prepareStatement(
                "REPLACE INTO BLOCKCHAIN_NODE_INFO (CODE, FORK_CODE, NAME, DESCRIPTION, UNITS, TX_FEE_UNITS, IP, PORT, FORWARDED_PORT, RPC_UN, RPC_PW, CLASS_NAME, DIFFICULTY_SCALING, REWARD_SCALING, FEE_SCALING, AMOUNT_SCALING, ZMQ_PORT, ENABLED) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        ps.setString(1, blockchainNodeInfo.code);
        ps.setString(2, blockchainNodeInfo.forkCode);
        ps.setString(3, blockchainNodeInfo.name);
        ps.setString(4, blockchainNodeInfo.description);
        ps.setString(5, blockchainNodeInfo.units);
        ps.setString(6, blockchainNodeInfo.txFeeUnits);
        ps.setString(7, blockchainNodeInfo.ip);
        ps.setInt(8, blockchainNodeInfo.port);
        ps.setInt(9, blockchainNodeInfo.forwardedPort);
        ps.setString(10, blockchainNodeInfo.rpcUn);
        ps.setString(11, blockchainNodeInfo.rpcPw);
        ps.setString(12, blockchainNodeInfo.className);
        ps.setInt(13, blockchainNodeInfo.difficultyScaling);
        ps.setInt(14, blockchainNodeInfo.rewardScaling);
        ps.setInt(15, blockchainNodeInfo.feeScaling);
        ps.setInt(16, blockchainNodeInfo.amountScaling);
        ps.setInt(17, blockchainNodeInfo.zmqPort);
        ps.setBoolean(18, blockchainNodeInfo.enabled);
        ps.executeUpdate();

        ps = connection.prepareStatement("Select MAX(CODE) FROM BLOCKCHAIN_NODE_INFO");
        ResultSet rs = ps.executeQuery();

        if (storedAutoCommitValue) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        return blockchainNodeInfo;
    }

    public static BlockchainNodeInfo updateBlockchainNodeInfo(Connection connection, BlockchainNodeInfo blockchainNodeInfo) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE BLOCKCHAIN_NODE_INFO SET FORK_CODE = ?, NAME = ?, DESCRIPTION = ?, UNITS = ?, TX_FEE_UNITS = ?, IP = ?, PORT = ?, FORWARDED_PORT = ?, RPC_UN = ?, RPC_PW = ?, CLASS_NAME = ?, DIFFICULTY_SCALING = ?, REWARD_SCALING = ?, FEE_SCALING = ?, AMOUNT_SCALING = ?, ZMQ_PORT = ?, ENABLED = ? WHERE CODE = ?");

        ps.setString(1, blockchainNodeInfo.forkCode);
        ps.setString(2, blockchainNodeInfo.name);
        ps.setString(3, blockchainNodeInfo.description);
        ps.setString(4, blockchainNodeInfo.units);
        ps.setString(5, blockchainNodeInfo.txFeeUnits);
        ps.setString(6, blockchainNodeInfo.ip);
        ps.setInt(7, blockchainNodeInfo.port);
        ps.setInt(8, blockchainNodeInfo.forwardedPort);
        ps.setString(9, blockchainNodeInfo.rpcUn);
        ps.setString(10, blockchainNodeInfo.rpcPw);
        ps.setString(11, blockchainNodeInfo.className);
        ps.setInt(12, blockchainNodeInfo.difficultyScaling);
        ps.setInt(13, blockchainNodeInfo.rewardScaling);
        ps.setInt(14, blockchainNodeInfo.feeScaling);
        ps.setInt(15, blockchainNodeInfo.amountScaling);
        ps.setInt(16, blockchainNodeInfo.zmqPort);
        ps.setBoolean(18, blockchainNodeInfo.enabled);
        ps.setString(19, blockchainNodeInfo.code);
        ps.executeUpdate();

        return blockchainNodeInfo;
    }

    public static void deleteAll(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("TRUNCATE TABLE BLOCKCHAIN_NODE_INFO");
        ps.executeUpdate();
    }

    public static BlockchainNodeInfo getBlockchainNodeInfo(Connection connection, String code) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(primaryKeySelect);

        ps.setString(1, code);
        ResultSet rs = ps.executeQuery();

        if (rs.next())
            return new BlockchainNodeInfo(rs);
        else
            return null;
    }

    @Override
    public void write(DataOutput dout) throws SerializationException {
        try {
            DataStreamUtilities.writeString(dout, code);

            DataStreamUtilities.writeString(dout, forkCode);

            DataStreamUtilities.writeString(dout, name);

            DataStreamUtilities.writeString(dout, description);

            DataStreamUtilities.writeString(dout, units);

            DataStreamUtilities.writeString(dout, txFeeUnits);

            DataStreamUtilities.writeString(dout, ip);

            DataStreamUtilities.writeInt(dout, port);
            DataStreamUtilities.writeInt(dout, forwardedPort);
            DataStreamUtilities.writeString(dout, rpcUn);

            DataStreamUtilities.writeString(dout, rpcPw);

            DataStreamUtilities.writeString(dout, className);

            DataStreamUtilities.writeInt(dout, difficultyScaling);
            DataStreamUtilities.writeInt(dout, rewardScaling);
            DataStreamUtilities.writeInt(dout, feeScaling);
            DataStreamUtilities.writeInt(dout, amountScaling);
            DataStreamUtilities.writeInt(dout, zmqPort);
            DataStreamUtilities.writeBoolean(dout, enabled);
        } catch (Throwable t) {
            throw new SerializationException("Unable To Serialize", t);
        }
    }

    @Override
    public void read(DataInput din) throws SerializationException {
        try {
            code = DataStreamUtilities.readString(din);
            forkCode = DataStreamUtilities.readString(din);
            name = DataStreamUtilities.readString(din);
            description = DataStreamUtilities.readString(din);
            units = DataStreamUtilities.readString(din);
            txFeeUnits = DataStreamUtilities.readString(din);
            ip = DataStreamUtilities.readString(din);
            port = DataStreamUtilities.readInt(din);
            forwardedPort = DataStreamUtilities.readInt(din);
            rpcUn = DataStreamUtilities.readString(din);
            rpcPw = DataStreamUtilities.readString(din);
            className = DataStreamUtilities.readString(din);
            difficultyScaling = DataStreamUtilities.readInt(din);
            rewardScaling = DataStreamUtilities.readInt(din);
            feeScaling = DataStreamUtilities.readInt(din);
            amountScaling = DataStreamUtilities.readInt(din);
            zmqPort = DataStreamUtilities.readInt(din);
            enabled = DataStreamUtilities.readBoolean(din);
        } catch (Throwable t) {
            throw new SerializationException("Unable To Serialize", t);
        }
    }
}