package it.polito.tdp.newufosightings.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.polito.tdp.newufosightings.model.Adiacenza;
import it.polito.tdp.newufosightings.model.Sighting;
import it.polito.tdp.newufosightings.model.State;

public class NewUfoSightingsDAO {

	public List<Sighting> loadAllSightings() {
		String sql = "SELECT * FROM sighting";
		List<Sighting> list = new ArrayList<>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);	
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(new Sighting(res.getInt("id"), res.getTimestamp("datetime").toLocalDateTime(),
						res.getString("city"), res.getString("state"), res.getString("country"), res.getString("shape"),
						res.getInt("duration"), res.getString("duration_hm"), res.getString("comments"),
						res.getDate("date_posted").toLocalDate(), res.getDouble("latitude"),
						res.getDouble("longitude")));
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return list;
	}

	public List<State> loadAllStates(Map<String, State> idMap) {
		String sql = "SELECT * FROM state";
		List<State> result = new ArrayList<State>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				State state = new State(rs.getString("id"), rs.getString("Name"), rs.getString("Capital"),
						rs.getDouble("Lat"), rs.getDouble("Lng"), rs.getInt("Area"), rs.getInt("Population"),
						rs.getString("Neighbors"));
				result.add(state);
				idMap.put(state.getId(), state);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<String> loadAllShapes(Integer anno) {
		String sql = "SELECT DISTINCT s.shape " + 
				"FROM sighting s " + 
				"WHERE YEAR(s.DATETIME)=? ";
		List<String> result = new ArrayList<String>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				result.add(rs.getString("s.shape"));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

	}

	public List<Adiacenza> getEdges() {
		
		String sql = "SELECT n.state1, n.state2 " + 
				"FROM neighbor n ";
		List<Adiacenza> result = new ArrayList<Adiacenza>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				result.add(new Adiacenza(rs.getString("n.state1"), rs.getString("n.state2"), 0));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		

	}

	public double getPeso(int anno, String forma, Adiacenza a) {
		String sql = "SELECT DISTINCT s1.id " + 
				"FROM sighting s1, sighting s2 " + 
				"WHERE s1.state=? " + 
				"AND s2.state=? " + 
				"AND s1.shape=? AND s2.shape=s1.shape " + 
				"AND YEAR(s1.DATETIME)=? AND YEAR(s2.DATETIME)= YEAR(s1.DATETIME) " + 
				"UNION " + 
				"SELECT DISTINCT s2.id " + 
				"FROM sighting s1, sighting s2 " + 
				"WHERE s1.state=? " + 
				"AND s2.state=? " + 
				"AND s1.shape=? AND s2.shape=s1.shape " + 
				"AND YEAR(s1.DATETIME)=? AND YEAR(s2.DATETIME)= YEAR(s1.DATETIME) ";
		double count=0;

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1,a.getId1());
			st.setString(2,a.getId2());
			st.setString(3,forma);
			st.setInt(4, anno);
			st.setString(5,a.getId1());
			st.setString(6,a.getId2());
			st.setString(7,forma);
			st.setInt(8, anno);
			
			ResultSet rs = st.executeQuery();

			while(rs.next())
			{	
			count++;
			}
			conn.close();
			
			a.setPeso(count);
			return count;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

}
