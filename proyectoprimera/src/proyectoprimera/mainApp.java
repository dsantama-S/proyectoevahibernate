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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.Date;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import reverse.*;



public class mainApp {

	//Voy a guardar en una clase llamada Empleado el procesamiento de datos del fichero binario(contabilidad) y de la base de datos(rrhh)
	//cuya cadena de conexion es "jdbc:mariadb://localhost:3306/db1eva","root", "root"
	//y con hibernate voy a insertar los datos en la base de datos(listaempleados) que se nos ha aportado con la cadena de conexion en prueba
	//"jdbc:mysql://localhost:3306/db1evaluacion","root", "root"
	//Esta conexion generaá  la clase listampleados donde estaran insertados los datos y tendrá un método público, que permite generar por
	//consola el listado consolidado de la información por mes, este último argumento del método
	
	
	public static void main(String[] args) throws FileNotFoundException, ParseException{
		ArrayList<Empleado> empleados = new ArrayList<Empleado>();
		datosrrhh(empleados);
		contabilidad(empleados);
		datosamysql(empleados);
		quedatos(empleados);
	}

	private static void quedatos(ArrayList<Empleado> empleados) {
		Scanner sc = new Scanner(System.in);
		System.out.println("¿Que mes desea buscar?");
		String mes = sc.next();
		System.out.println("¿Que año desea buscar?");
		String periodo= sc.next();
		for(int i = 0; i < empleados.size(); i++)
			empleados.get(i).GenerarDato(mes, periodo);
	}

	private static void datosamysql(ArrayList<Empleado> empleados) throws ParseException {
		// TODO Auto-generated method stub
		Configuration configuracion = new Configuration();
		configuracion.configure();
		
		SessionFactory sesion = configuracion.buildSessionFactory();
		Session ss = sesion.openSession();
		Transaction trans = ss.beginTransaction();
		
		for(int i = 0; i < empleados.size(); i++)
		{
			ListaempleadosId n = new ListaempleadosId();
			Listaempleados idn = new Listaempleados();
			n.setMatricula(empleados.get(i).getMatricula());
			n.setTotventas(empleados.get(i).getVentamensual());
			n.setTotgastos(empleados.get(i).getTotalgastos());
			n.setApenom(empleados.get(i).getApenom());	
			n.setDepartamento(empleados.get(i).getDepartamento());
			Date fecha = new Date(0).valueOf(empleados.get(i).getFechames());
			n.setFecha(fecha);
			idn.setId(n);
			ss.save(idn);
		}
		trans.commit();
		sesion.close();
		System.out.println("Fin de la importacion");
		
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
		
	}

	private static void datosrrhh(ArrayList<Empleado> empleados) throws ParseException {
		try {
			Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/db1eva","root", "root");
			Statement sent = conn.createStatement();
			String ssql = "SELECT * FROM RRHHVENTAS";
			ResultSet rs = sent.executeQuery(ssql);
			String fecha = "null";
			while(rs.next())
			{
				if(estaMatricula(rs.getString("MATRICULA"), empleados) == false) {
					Empleado n = new Empleado();
					n.setMatricula(rs.getString("MATRICULA"));
					n.setVentamensual(sumaventasemanal(rs.getString("MATRICULA"), sent, ssql));
					n.setApenom(rs.getString("APENOM"));	
					n.setDepartamento(rs.getString("DEPARTAMENTO"));
					fecha = rs.getString("SEMANA");
					n.setFechames(fecha);
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
