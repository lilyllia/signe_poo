package br.com.signe.payroll;

import jakarta.persistence.Embeddable;

@Embeddable
public class ProcedureValue {

    private String procedureName;
    private double value;

    protected ProcedureValue() {}

    public ProcedureValue(String procedureName, double value) {
        this.procedureName = procedureName;
        this.value = value;
    }

    public String getProcedureName() { return procedureName; }
    public double getValue() { return value; }
}

