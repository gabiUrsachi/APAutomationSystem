package org.example.utils;

public enum Roles {
    ADMIN, // create users with different roles
    BUYER_I, // view/create/send/edit PO
    BUYER_II,  // view/pay INVOICE
    SUPPLIER_I, // view/create/edit/send INVOICE, view approved PO
    SUPPLIER_II // approve/reject PO
}
