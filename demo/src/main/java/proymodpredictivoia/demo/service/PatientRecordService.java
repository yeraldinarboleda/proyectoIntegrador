package proymodpredictivoia.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proymodpredictivoia.demo.model.CardioResults;
import proymodpredictivoia.demo.model.LabResults;
import proymodpredictivoia.demo.model.MedicalData;
import proymodpredictivoia.demo.model.PatientRecord;
import proymodpredictivoia.demo.model.PersonalData;
import proymodpredictivoia.demo.model.RiskFactors;
import proymodpredictivoia.demo.repository.PatientRecordRepository;
import proymodpredictivoia.demo.repository.PersonalDataRepository;

@Service
public class PatientRecordService {

    @Autowired private PatientRecordRepository recordRepository;
    @Autowired private PersonalDataRepository personalDataRepository;

    public PatientRecord linkLabResults(String documentId, LabResults labResults) {
        // Buscar los datos personales
        PersonalData personalData = personalDataRepository.findByDocumentId(documentId)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Buscar o crear el registro de paciente
        Optional<PatientRecord> optionalRecord = recordRepository.findByPersonalData(personalData);
        PatientRecord record;
        if (optionalRecord.isPresent()) {
            record = optionalRecord.get();
        } else {
            record = new PatientRecord();
            record.setPersonalData(personalData);
        }

        // Asignar resultados de laboratorio
        record.setLabResults(labResults);
        return recordRepository.save(record);
    }



    public PatientRecord linkCardioResults(String documentId, CardioResults cardioResults) {
    
        // Buscar los datos personales
        PersonalData personalData = personalDataRepository.findByDocumentId(documentId)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Buscar o crear el registro de paciente
        Optional<PatientRecord> optionalRecord = recordRepository.findByPersonalData(personalData);
        PatientRecord record;
        if (optionalRecord.isPresent()) {
            record = optionalRecord.get();
        } else {
            record = new PatientRecord();
            record.setPersonalData(personalData);
        }

        // Asignar resultados de laboratorio
        record.setCardioResults(cardioResults);
        return recordRepository.save(record);
    }


    public PatientRecord linkRiskFactors(String documentId, RiskFactors riskFactors) {
    // Buscar los datos personales
        PersonalData personalData = personalDataRepository.findByDocumentId(documentId)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Buscar o crear el registro de paciente
        Optional<PatientRecord> optionalRecord = recordRepository.findByPersonalData(personalData);
        PatientRecord record;
        if (optionalRecord.isPresent()) {
            record = optionalRecord.get();
        } else {
            record = new PatientRecord();
            record.setPersonalData(personalData);
        }

        // Asignar factores de riesgo
        record.setRiskFactors(riskFactors);
        return recordRepository.save(record);
    }


    

    public PatientRecord linkMedicalData(String documentId, MedicalData medicalData) {
    
        // Buscar los datos personales
        PersonalData personalData = personalDataRepository.findByDocumentId(documentId)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Buscar o crear el registro de paciente
        Optional<PatientRecord> optionalRecord = recordRepository.findByPersonalData(personalData);
        PatientRecord record;
        if (optionalRecord.isPresent()) {
            record = optionalRecord.get();
        } else {
            record = new PatientRecord();
            record.setPersonalData(personalData);
        }

        // Asignar resultados de laboratorio
        record.setMedicalData(medicalData);
        return recordRepository.save(record);
    }
}

