package it.polito.postiletto.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.polito.postiletto.model.Dati;


public class DatiDAO {

	public List<Dati> getListaDati(){
		String sql = "SELECT accettazione, dimissione, reparto FROM ricoveri";
		List<Dati> listaDati = new ArrayList<>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			

			while (res.next()) {
				Dati d = new Dati(res.getDate("accettazione").toLocalDate(), res.getDate("dimissione").toLocalDate(), res.getString("reparto"));
				listaDati.add(d);
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return listaDati;
	}
	
	public int getNMaxPosti(String reparto) {
		String sql = "SELECT nmax FROM postimassimireparti WHERE reparto=?";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			st.setString(1, reparto);
			
			ResultSet res = st.executeQuery();

			while (res.next()) {
				return res.getInt("nmax");
			}

			conn.close();
			return -1;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int occupazioneRepartoPrev(LocalDate data, String reparto) {
		final String sql="SELECT COUNT(*) AS numRighe FROM ricoveri WHERE reparto=? && accettazione<=? && dimissione>=?";
		
		Connection conn = DBConnect.getConnection();
		PreparedStatement st;
		
		
		try {
			st = conn.prepareStatement(sql);

			st.setString(1, reparto);
			st.setDate(2, Date.valueOf(data));
			st.setDate(3, Date.valueOf(data));
			
			//NON FUNZIONA CORRETTAMENTE LA QUERY AMCHE SE SU HEIDI VA
			
			ResultSet rs = st.executeQuery();
			rs.next();


			System.out.println(rs.getInt("numRighe"));
			
			int res= rs.getInt("numRighe");

			System.out.println(Date.valueOf(data)+" "+res+"\n");
			conn.close();
			return  res;

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
