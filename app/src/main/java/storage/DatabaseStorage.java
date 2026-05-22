package storage;

import domain.entity.*;
import domain.enums.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public class DatabaseStorage implements Storage {
    public DatabaseStorage() {
        createTablesIfNotExists();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUser(),
                DatabaseConfig.getPassword()
        );
    }


    @Override
    public Map<String, User> loadAllUsers() {
        Map<String, User> users = new HashMap<>();
        String sql = "SELECT id, login, password_hash, created_at, updated_at FROM users";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setLogin(rs.getString("login"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                if (rs.getTimestamp("updated_at") != null) {
                    user.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
                }
                users.put(user.getLogin(), user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public void saveUser(User user) {
        String sql = "INSERT INTO users (id, login, password_hash, created_at, updated_at) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET password_hash = ?, updated_at = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, user.getId());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getPasswordHash());
            ps.setTimestamp(4, Timestamp.from(user.getCreatedAt()));
            ps.setTimestamp(5, Timestamp.from(user.getUpdatedAt()));
            ps.setString(6, user.getPasswordHash());
            ps.setTimestamp(7, Timestamp.from(Instant.now()));
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Map<Long, Solution> loadAllSolutions() {
        Map<Long, Solution> solutions = new HashMap<>();
        String sql = "SELECT id, name, concentration, concentration_unit, solvent, owner_username, created_at, updated_at FROM solutions";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Solution s = new Solution();
                s.setId(rs.getLong("id"));
                s.setName(rs.getString("name"));
                s.setConcentration(rs.getDouble("concentration"));
                s.setConcentrationUnit(SolutionConcentrationUnit.valueOf(rs.getString("concentration_unit")));
                s.setSolvent(rs.getString("solvent"));
                s.setOwnerUsername(rs.getString("owner_username"));
                s.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                if (rs.getTimestamp("updated_at") != null) {
                    s.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
                }
                solutions.put(s.getId(), s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return solutions;
    }

    @Override
    public void saveSolution(Solution solution) {
        String sql = "INSERT INTO solutions (id, name, concentration, concentration_unit, solvent, owner_username, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name = ?, concentration = ?, concentration_unit = ?, solvent = ?, updated_at = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, solution.getId());
            ps.setString(2, solution.getName());
            ps.setDouble(3, solution.getConcentration());
            ps.setString(4, solution.getConcentrationUnit().name());
            ps.setString(5, solution.getSolvent());
            ps.setString(6, solution.getOwnerUsername());
            ps.setTimestamp(7, Timestamp.from(solution.getCreatedAt()));
            ps.setTimestamp(8, Timestamp.from(solution.getUpdatedAt()));
            ps.setString(9, solution.getName());
            ps.setDouble(10, solution.getConcentration());
            ps.setString(11, solution.getConcentrationUnit().name());
            ps.setString(12, solution.getSolvent());
            ps.setTimestamp(13, Timestamp.from(Instant.now()));
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSolution(long id) {
        String sql = "DELETE FROM solutions WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Map<Long, Preparation> loadAllPreparations() {
        Map<Long, Preparation> preparations = new HashMap<>();
        String sql = "SELECT id, solution_id, final_quantity, final_unit, comment, owner_username, prepared_at, created_at, updated_at FROM preparations";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Preparation p = new Preparation();
                p.setId(rs.getLong("id"));
                p.setSolutionId(rs.getLong("solution_id"));
                p.setFinalQuantity(rs.getDouble("final_quantity"));
                p.setFinalUnit(FinalQuantityUnit.valueOf(rs.getString("final_unit")));
                p.setComment(rs.getString("comment"));
                p.setOwnerUsername(rs.getString("owner_username"));
                p.setPreparedAt(rs.getTimestamp("prepared_at").toInstant());
                p.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                if (rs.getTimestamp("updated_at") != null) {
                    p.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
                }
                preparations.put(p.getId(), p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparations;
    }

    @Override
    public void savePreparation(Preparation preparation) {
        String sql = "INSERT INTO preparations (id, solution_id, final_quantity, final_unit, comment, owner_username, prepared_at, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET final_quantity = ?, final_unit = ?, comment = ?, updated_at = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, preparation.getId());
            ps.setLong(2, preparation.getSolutionId());
            ps.setDouble(3, preparation.getFinalQuantity());
            ps.setString(4, preparation.getFinalUnit().name());
            ps.setString(5, preparation.getComment());
            ps.setString(6, preparation.getOwnerUsername());
            ps.setTimestamp(7, Timestamp.from(preparation.getPreparedAt()));
            ps.setTimestamp(8, Timestamp.from(preparation.getCreatedAt()));
            ps.setTimestamp(9, Timestamp.from(preparation.getUpdatedAt()));
            ps.setDouble(10, preparation.getFinalQuantity());
            ps.setString(11, preparation.getFinalUnit().name());
            ps.setString(12, preparation.getComment());
            ps.setTimestamp(13, Timestamp.from(Instant.now()));
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePreparation(long id) {
        String sql = "DELETE FROM preparations WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Map<Long, PreparationComponent> loadAllComponents() {
        Map<Long, PreparationComponent> components = new HashMap<>();
        String sql = "SELECT id, preparation_id, batch_id, quantity, unit, created_at FROM preparation_components";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PreparationComponent c = new PreparationComponent();
                c.setId(rs.getLong("id"));
                c.setPreparationId(rs.getLong("preparation_id"));
                c.setBatchId(rs.getLong("batch_id"));
                c.setQuantity(rs.getDouble("quantity"));
                c.setUnit(ComponentUnit.valueOf(rs.getString("unit")));
                c.setCreatedAt(rs.getTimestamp("created_at").toInstant());
                components.put(c.getId(), c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return components;
    }

    @Override
    public void saveComponent(PreparationComponent component) {
        String sql = "INSERT INTO preparation_components (id, preparation_id, batch_id, quantity, unit, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET quantity = ?, unit = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, component.getId());
            ps.setLong(2, component.getPreparationId());
            ps.setLong(3, component.getBatchId());
            ps.setDouble(4, component.getQuantity());
            ps.setString(5, component.getUnit().name());
            ps.setTimestamp(6, Timestamp.from(component.getCreatedAt()));
            ps.setDouble(7, component.getQuantity());
            ps.setString(8, component.getUnit().name());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteComponent(long id) {
        String sql = "DELETE FROM preparation_components WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTablesIfNotExists() {
        String sqlUsers = """
        CREATE TABLE IF NOT EXISTS users (
            id BIGINT PRIMARY KEY,
            login VARCHAR(64) UNIQUE NOT NULL,
            password_hash VARCHAR(256) NOT NULL,
            created_at TIMESTAMP NOT NULL,
            updated_at TIMESTAMP
        )
    """;

        String sqlSolutions = """
        CREATE TABLE IF NOT EXISTS solutions (
            id BIGINT PRIMARY KEY,
            name VARCHAR(128) NOT NULL,
            concentration DOUBLE PRECISION NOT NULL,
            concentration_unit VARCHAR(20) NOT NULL,
            solvent VARCHAR(64),
            owner_username VARCHAR(64) NOT NULL,
            created_at TIMESTAMP NOT NULL,
            updated_at TIMESTAMP
        )
    """;

        String sqlPreparations = """
        CREATE TABLE IF NOT EXISTS preparations (
            id BIGINT PRIMARY KEY,
            solution_id BIGINT NOT NULL,
            final_quantity DOUBLE PRECISION NOT NULL,
            final_unit VARCHAR(10) NOT NULL,
            comment VARCHAR(128),
            owner_username VARCHAR(64) NOT NULL,
            prepared_at TIMESTAMP NOT NULL,
            created_at TIMESTAMP NOT NULL,
            updated_at TIMESTAMP
        )
    """;

        String sqlComponents = """
        CREATE TABLE IF NOT EXISTS preparation_components (
            id BIGINT PRIMARY KEY,
            preparation_id BIGINT NOT NULL,
            batch_id BIGINT NOT NULL,
            quantity DOUBLE PRECISION NOT NULL,
            unit VARCHAR(10) NOT NULL,
            created_at TIMESTAMP NOT NULL
        )
    """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlUsers);
            stmt.execute(sqlSolutions);
            stmt.execute(sqlPreparations);
            stmt.execute(sqlComponents);

            System.out.println("Таблицы созданы/проверены");

        } catch (SQLException e) {
            System.err.println("Ошибка создания таблиц: " + e.getMessage());
        }
    }
}