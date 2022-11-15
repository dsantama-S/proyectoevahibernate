package proyectoprimera;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;



public class mainApp {

	public static void main(String[] args) throws FileNotFoundException{
		ArrayList<Empleado> empleados = new ArrayList<Empleado>();
		datosrrhh(empleados);
		contabilidad(empleados);
	}

	private static void contabilidad(ArrayList<Empleado> empleados) throws FileNotFoundException {
		String archivo = ("C:" + File.separator + "Users" + File.separator + 
				"david"+ File.separator + "Desktop" + File.separator +"Archivostexto" + File.separator + "01102022.dat");
		File fichero = new File(archivo);
		FileInputStream files = new FileInputStream(fichero);
		DataInputStream dat = new DataInputStream(files);
		String matri;
		double salario;
		double gastos;
		int i = 0;
		try {
			while(i < empleados.size())
			{
				matri = dat.readUTF();
				salario = dat.readDouble();
				gastos = dat.readDouble();
				int posicion = buscaMatricula(matri, empleados);
				Empleado n = empleados.get(posicion);
				n.setTotalgastos(gastos + salario);
				i++;
			}
		}catch(IOException e) {
		}
		for(int c = 0; c < empleados.size(); c++)
		{
			System.out.println(empleados.get(c));
		}
		
	}

	private static void datosrrhh(ArrayList<Empleado> empleados) {
		try {
			Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/db1eva","root", "root");
			Statement sent = conn.createStatement();
			String ssql = "SELECT * FROM RRHHVENTAS";
			ResultSet rs = sent.executeQuery(ssql);
			while(rs.next())
			{
				if(estaMatricula(rs.getString("MATRICULA"), empleados) == false) {
					Empleado n = new Empleado();
					n.setMatricula(rs.getString("MATRICULA"));
					n.setVentamensual(sumaventasemanal(rs.getString("MATRICULA"), sent, ssql));
					n.setApenom(rs.getString("APENOM"));	
					n.setDepartamento(rs.getString("DEPARTAMENTO"));	
					empleados.add(n);
				}
			}
			rs.close();
			sent.close();
			conn.close();
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
	}
	private static double sumaventasemanal(String matricula, Statement sent, String ssql) throws SQLException {
		ResultSet rs = sent.executeQuery(ssql);
		double ventamensual = 0;
		while(rs.next())
		{
			if(rs.getString("MATRICULA").equalsIgnoreCase(matricula)){
				ventamensual += rs.getDouble("VENTAS");
			}
		}
		return ventamensual;
	}

	private static boolean estaMatricula(String matricula, ArrayList<Empleado> empleados)
	{
		boolean ret = false;
		int i = 0;
		if(empleados.size() > 0)
		{
			while(i < empleados.size())
			{
				if(matricula.equalsIgnoreCase(empleados.get(i).getMatricula()))
					ret = true;
				i++;
			}
		}
		return ret;
	}
	
	private static int buscaMatricula(String matricula, ArrayList<Empleado> empleados)
	{
		int pos = 0;
		int i = 0;
		while(i < empleados.size())
		{
			if(matricula.equalsIgnoreCase(empleados.get(i).getMatricula()))
				pos = i;
			i++;
		}
		return pos;
	}

}
