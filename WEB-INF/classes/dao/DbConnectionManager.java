// © 2024 Maxime MORGE <maxime.morge@univ-lyon1.fr>
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe singleton sur la source de données
 */
public class DbConnectionManager{
  private static final String URL = "jdbc:postgresql://localhost:5432/fanfarehub";
  private static final String USER = "postgres";
  private static final String PASSWORD = "postgres";
  private static DbConnectionManager instance;// Singleton de DbConnectionManager
  private DbConnectionManager(){// Constructeur privé pour empêcher l'instanciation directe
    try{// Charger le driver PostgreSQL
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e){ throw new RuntimeException("Pb de driver", e); }
  }
  public Connection getConnection() throws SQLException{// Obtenir une connexion à la bdd
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }
  public static DbConnectionManager getInstance(){// Obtenir l'instance unique de DbConnectionManager
    if (instance == null) {
      synchronized (DbConnectionManager.class){
        if (instance == null) {
          instance = new DbConnectionManager();
        }
      }
    }
    return instance;
  }
}