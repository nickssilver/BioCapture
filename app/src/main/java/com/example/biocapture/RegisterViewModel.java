package com.example.biocapture;

import java.security.Permissions;
import java.util.List;

public class RegisterViewModel {
    private Biousers user;
    private List<Permissions> permissions;

    // Getters and setters for user and permissions
    public Biousers getUser() {
        return user;
    }

    public void setUser(Biousers user) {
        this.user = user;
    }

    public List<Permissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permissions> permissions) {
        this.permissions = permissions;
    }
}

