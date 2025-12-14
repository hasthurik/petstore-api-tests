package com.example;

import com.example.model.Pet;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import static io.restassured.http.ContentType.JSON;


import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class PetApiTest extends BaseTest {

    private Pet createPet(String status) {
        Pet pet = new Pet();
        pet.id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        pet.name = "pet-" + pet.id;
        pet.status = status;
        pet.photoUrls = List.of();
        return pet;
    }

    @Test
    void createAndGetPet() {
        Pet pet = createPet("available");

        long petId =
                given()
                        .contentType(JSON)
                        .body(pet)
                        .when()
                        .post("/pet")
                        .then()
                        .statusCode(200)
                        .body("name", equalTo(pet.name))
                        .extract()
                        .path("id");

        when()
                .get("/pet/{id}", petId)
                .then()
                .statusCode(200)
                .body("name", equalTo(pet.name))
                .body("status", equalTo("available"));
    }


    @Test
    void updatePetWithPut() {
        Pet pet = createPet("available");

        given()
                .contentType(JSON)
                .body(pet)
                .post("/pet")
                .then()
                .statusCode(200);

        pet.status = "sold";
        pet.name = pet.name + "-updated";

        given()
                .contentType(JSON)
                .body(pet)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .body("status", equalTo("sold"));
    }

    @Test
    void deletePet() {
        Pet pet = createPet("available");

        long petId =
                given()
                        .contentType(JSON)
                        .body(pet)
                        .when()
                        .post("/pet")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("id");

        delete("/pet/{id}", petId)
                .then()
                .statusCode(200);

        get("/pet/{id}", petId)
                .then()
                .statusCode(anyOf(is(404), is(400)));
    }


    @Test
    void findByStatus() {
        given()
                .queryParam("status", "pending")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .body("status", everyItem(equalTo("pending")));
    }

    @Test
    void getNonExistingPet() {
        long nonExistingId = 999999999999L;

        get("/pet/{id}", nonExistingId)
                .then()
                .statusCode(anyOf(is(404), is(400)));
    }

    @Test
    void createPetWithoutName() {
        Pet pet = new Pet();
        pet.id = Math.abs(UUID.randomUUID().getMostSignificantBits());
        pet.status = "available";
        pet.photoUrls = List.of();

        given()
                .contentType(JSON)
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200); //api НЕ валидирует обязательные поля поэтому статус-код 200
    }

}
