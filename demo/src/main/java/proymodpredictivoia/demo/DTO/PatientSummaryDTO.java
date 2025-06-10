package proymodpredictivoia.demo.DTO;

import proymodpredictivoia.demo.model.*;
import java.util.List;

public class PatientSummaryDTO {
    private PersonalData personalData;
    private List<MedicalData> medicalData;
    private List<LabResults> labResults;
    private List<CardioResults> cardioResults;
    private List<RiskFactors> riskFactors;

    public PersonalData getPersonalData() {
        return personalData;
    }

    public void setPersonalData(PersonalData personalData) {
        this.personalData = personalData;
    }

    public List<MedicalData> getMedicalData() {
        return medicalData;
    }

    public void setMedicalData(List<MedicalData> medicalData) {
        this.medicalData = medicalData;
    }

    public List<LabResults> getLabResults() {
        return labResults;
    }

    public void setLabResults(List<LabResults> labResults) {
        this.labResults = labResults;
    }

    public List<CardioResults> getCardioResults() {
        return cardioResults;
    }

    public void setCardioResults(List<CardioResults> cardioResults) {
        this.cardioResults = cardioResults;
    }

    public List<RiskFactors> getRiskFactors() {
        return riskFactors;
    }

    public void setRiskFactors(List<RiskFactors> riskFactors) {
        this.riskFactors = riskFactors;
    }
}
