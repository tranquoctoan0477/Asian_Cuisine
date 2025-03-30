package com.example.appasiancuisine.data.dto;

import java.util.List;

public class CheckoutDTO {

    private String address;             // cột address
    private String phone_number;        // cột phone_number
    private String address_note;        // cột address_note
    private String voucher_code;        // cột voucher_code
    private List<CheckoutItemDTO> items; // xử lý trong bảng order_items

    public CheckoutDTO() {}

    public CheckoutDTO(String address, String phone_number, String address_note, String voucher_code, List<CheckoutItemDTO> items) {
        this.address = address;
        this.phone_number = phone_number;
        this.address_note = address_note;
        this.voucher_code = voucher_code;
        this.items = items;
    }

    // Getter & Setter

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAddress_note() {
        return address_note;
    }

    public void setAddress_note(String address_note) {
        this.address_note = address_note;
    }

    public String getVoucher_code() {
        return voucher_code;
    }

    public void setVoucher_code(String voucher_code) {
        this.voucher_code = voucher_code;
    }

    public List<CheckoutItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CheckoutItemDTO> items) {
        this.items = items;
    }
}
