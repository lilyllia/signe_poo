package br.com.signe.payroll;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProcedureValue {

    @Column(name = "procedure_name", nullable = false)
    private String procedureName;

    @Column(name = "procedure_value", nullable = false)
    private double value;

    protected ProcedureValue() {}

    public ProcedureValue(String procedureName, double value) {
        this.procedureName = procedureName;
        this.value = value;
    }

    public String getProcedureName() { return procedureName; }
    public double getValue() { return value; }
}