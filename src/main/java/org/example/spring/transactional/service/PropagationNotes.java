package org.example.spring.transactional.service;

public class PropagationNotes {
    /**
     * required, varsa mevcut transactiona katilir, yoksa yeni transaction acar.
     *
     * requires new, her zaman yeni transaction acar, mevcut transactioni bekletir.
     *
     * nested, mevcut transaction icinde savepoint acar, ic islem geri alinabilir.
     *
     * supports, transaction varsa katilir, yoksa transactionsiz calisir.
     *
     * mandatory, mevcut transaction ister, yoksa hata verir.
     *
     * not supported, mevcut transactioni bekletir, transactionsiz calisir.
     *
     * never, transaction varsa hata verir, yoksa transactionsiz calisir.
     *
     * */
}
