package com.example;

import com.example.model.Pet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import static io.restassured.http.ContentType.JSON;


import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class PetApiTest extends BaseTest {


    private Pet testPet;
    private long testPetId;

    private Pet createPet(String status) {
        Pet pet = new Pet();
        pet.id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        pet.name = "pet-" + pet.id;
        pet.status = status;
        pet.photoUrls = List.of();
        return pet;
    }

    @BeforeEach
    void setupPetResource() {
        testPet = createPet("available");

        testPetId =
                given()
                        .contentType(JSON)
                        .body(testPet)
                        .when()
                        .post("/pet")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("id");
        testPet.id = testPetId;
    }

    @Test
    @DisplayName("Проверка создания Pet и последующего получения по ID")
    void createAndGetPet() {
        when()
                .get("/pet/{id}", testPetId)
                .then()
                .statusCode(200)
                .body("name", equalTo(testPet.name))
                .body("status", equalTo("available"));
    }


    @Test
    @DisplayName("Обновление существующего Pet с помощью PUT")
    void updatePetWithPut() {
        String newStatus = "sold";
        String newName = testPet.name + "-updated";

        testPet.status = newStatus;
        testPet.name = newName;

        given()
                .contentType(JSON)
                .body(testPet)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .body("name", equalTo(newName))
                .body("status", equalTo(newStatus));
    }

    @Test
    @DisplayName("Удаление Pet и проверка, что оно больше не существует")
    void deletePet() {

        delete("/pet/{id}", testPetId)
                .then()
                .statusCode(200);

        get("/pet/{id}", testPetId)
                .then()
                .statusCode(anyOf(is(404), is(400)));
    }


    @Test
    @DisplayName("Поиск Pet по статусу 'pending'")
    void findByStatus() {
        Pet pendingPet = createPet("pending");

        given()
                .contentType(JSON)
                .body(pendingPet)
                .post("/pet")
                .then()
                .statusCode(200);

        given()
                .queryParam("status", "pending")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .body("status", everyItem(equalTo("pending")));
    }

    @Test
    @DisplayName("Попытка получить несуществующий Pet")
    void getNonExistingPet() {
        long nonExistingId = 999999999999L;

        get("/pet/{id}", nonExistingId)
                .then()
                .statusCode(anyOf(is(404), is(400)));
    }

    @AfterEach
    void cleanup() {
        delete("/pet/{id}", testPetId)
                .then()
                .statusCode(anyOf(is(200), is(204), is(404)));
    }
}

