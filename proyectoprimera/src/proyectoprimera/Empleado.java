package proyectoprimera;

import java.util.Objects;

public class Empleado {
	private String matricula;
	private String apenom;
	private String departamento;
	private double ventamensual;
	private double totalgastos;
	
	public double getTotalgastos() {
		return totalgastos;
	}
	public void setTotalgastos(double totalgastos) {
		this.totalgastos = totalgastos;
	}
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public String getApenom() {
		return apenom;
	}
	public void setApenom(String apenom) {
		this.apenom = apenom;
	}
	public String getDepartamento() {
		return departamento;
	}
	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}
	public double getVentamensual() {
		return ventamensual;
	}
	public void setVentamensual(double ventamensual) {
		this.ventamensual = ventamensual;
	}
}
