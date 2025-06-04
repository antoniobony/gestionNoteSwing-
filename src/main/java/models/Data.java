package models;

import java.util.List;

public class Data {
    private Integer code;
    private Integer totalPages;
    private List<String> sort;
    private List<Etudiant> etudiants;
    private Integer currentPage;
    private  Integer totalElements;

    public Data() {
    }

    public Data(Integer code, Integer totalPages, List<String> sort, List<Etudiant> etudiants, Integer currentPage, Integer totalElements) {
        this.code = code;
        this.totalPages = totalPages;
        this.sort = sort;
        this.etudiants = etudiants;
        this.currentPage = currentPage;
        this.totalElements = totalElements;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<String> getSort() {
        return sort;
    }

    public void setSort(List<String> sort) {
        this.sort = sort;
    }

    public List<Etudiant> getEtudiants() {
        return etudiants;
    }

    public void setEtudiants(List<Etudiant> etudiants) {
        this.etudiants = etudiants;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }
}
