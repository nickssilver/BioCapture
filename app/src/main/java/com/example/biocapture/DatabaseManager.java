package com.example.biocapture;

import com.morpho.morphosmart.sdk.CustomInteger;
import com.morpho.morphosmart.sdk.MorphoDatabase;
import com.morpho.morphosmart.sdk.MorphoField;
import com.morpho.morphosmart.sdk.MorphoSmartException;
import com.morpho.morphosmart.sdk.MorphoUser;
import com.morpho.morphosmart.sdk.TemplateType;

import java.util.ArrayList;
import java.util.List;

import api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseManager {

    // Retrofit API service instance
    private ApiService apiService;

    public DatabaseManager(ApiService apiService) {
        this.apiService = apiService;
    }

    // Create Internal Database
    public void createInternalDatabase(MorphoDatabase morphoDatabase, int maxRecords, int maxFingersPerRecord, int maxFieldSize) {
        try {
            // Database creation
            int result = morphoDatabase.dbCreate(maxRecords, maxFingersPerRecord, TemplateType.getValue(maxFieldSize), 0, false);
            if (result != 0) {
                throw new MorphoSmartException("Error creating internal database. Error code: " + result);
            }
        } catch (MorphoSmartException e) {
            e.printStackTrace();
        }
    }

    // Method to fetch and add all records from the API to the database
    public void addAllRecordsFromApiToDatabase(final MorphoDatabase morphoDatabase) {
        apiService.getAllTemplatesFromDatabase().enqueue(new Callback<List<FingerprintTemplate>>() {
            @Override
            public void onResponse(Call<List<FingerprintTemplate>> call, Response<List<FingerprintTemplate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FingerprintTemplate> fingerprintTemplates = response.body();
                    for (FingerprintTemplate template : fingerprintTemplates) {
                        addRecordToDatabase(morphoDatabase, template);
                    }
                } else {
                    // Handle unsuccessful response
                    System.err.println("Failed to fetch fingerprint templates from API");
                }
            }

            @Override
            public void onFailure(Call<List<FingerprintTemplate>> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
            }
        });
    }

    // Method to add a single record to the database
    public void addRecordToDatabase(MorphoDatabase morphoDatabase, FingerprintTemplate fingerprintTemplate) {
        try {
            // Creating MorphoField objects for storing fingerprint data
            MorphoField studentIdField = new MorphoField();
            studentIdField.setName("student_id"); // Set field name
            studentIdField.setMaxSize(50); // Set maximum size
            // Set data for student ID field if available
            if (fingerprintTemplate.getStudentId() != null) {
                morphoDatabase.putField(studentIdField, new CustomInteger());
            }

            MorphoField studentNameField = new MorphoField();
            studentNameField.setName("student_name");
            studentNameField.setMaxSize(100);
            if (fingerprintTemplate.getStudentName() != null) {
                morphoDatabase.putField(studentNameField,new CustomInteger());
            }

            MorphoField classIdField = new MorphoField();
            classIdField.setName("class_id");
            classIdField.setMaxSize(20);
            if (fingerprintTemplate.getClassId() != null) {
                morphoDatabase.putField(classIdField, new CustomInteger());
            }

            MorphoField statusField = new MorphoField();
            statusField.setName("status");
            statusField.setMaxSize(20);
            if (fingerprintTemplate.getStatus() != null) {
                morphoDatabase.putField(statusField, new CustomInteger());
            }

            MorphoField arrearsField = new MorphoField();
            arrearsField.setName("arrears");
            arrearsField.setMaxSize(20);
            // Assuming arrears is stored as a double
            if (!Double.isNaN(fingerprintTemplate.getArrears())) {
                morphoDatabase.putField(arrearsField, new CustomInteger());
            }


            MorphoField fingerprint1Field = new MorphoField();
            fingerprint1Field.setName("fingerprint_1");
            fingerprint1Field.setMaxSize(1000);
            if (fingerprintTemplate.getFingerprint1() != null) {
                morphoDatabase.putField(fingerprint1Field, new CustomInteger());
            }

            MorphoField fingerprint2Field = new MorphoField();
            fingerprint2Field.setName("fingerprint_2");
            fingerprint2Field.setMaxSize(1000);
            if (fingerprintTemplate.getFingerprint2() != null) {
                morphoDatabase.putField(fingerprint2Field, new CustomInteger());
            }

            // Adding fields to the database
            morphoDatabase.putField(studentIdField, null);
            morphoDatabase.putField(studentNameField, null);
            morphoDatabase.putField(classIdField, null);
            morphoDatabase.putField(statusField, null);
            morphoDatabase.putField(arrearsField, null);
            morphoDatabase.putField(fingerprint1Field, null);
            morphoDatabase.putField(fingerprint2Field, null);


        } catch (MorphoSmartException e) {
            e.printStackTrace();
        }
    }
    // Method to query data from internal database
    public List<MorphoUser> queryDataFromInternalDB(MorphoDatabase internalDatabase, int fieldIndex, String searchDataToFind) {
        List<MorphoUser> users = new ArrayList<>();
        try {
            MorphoUser user = new MorphoUser();
            internalDatabase.dbQueryFirst(fieldIndex, searchDataToFind, user);

            while (user != null) {
                users.add(user);
                user = new MorphoUser();
                internalDatabase.dbQueryNext(user);
            }
        } catch (MorphoSmartException e) {
            // Handle MorphoSmartException
            e.printStackTrace();
        }
        return users;
    }


}
