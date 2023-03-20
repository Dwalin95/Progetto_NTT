package com.example.ntt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailGenderOnlyDTO {
    private String username;
    private String email;
    private String gender;

}


/**
 * DTO - Data Trasnfer Object
 * Best Practice
 * Mantenere i DTO semplici e leggeri:
 * un DTO dovrebbe contenere solo le informazioni necessarie per rappresentare
 * l'oggetto di dominio.
 *
 * Utilizzare nomi di campo descrittivi:
 * i nomi di campo dei DTO dovrebbero riflettere il loro contenuto in modo chiaro e descrittivo.
 *
 * Evitare campi obbligatori:
 * i campi obbligatori devono essere evitati, poiché possono causare errori
 * se non vengono forniti i dati corretti.
 *
 * Evitare campi vuoti: m
 * i campi vuoti dovrebbero essere evitati
 * per evitare errori nell'applicazione.
 *
 * Usare i tipi corretti:
 * i tipi di dato utilizzati nei DTO dovrebbero essere coerenti con
 * i tipi di dato dell'oggetto di dominio.
 */