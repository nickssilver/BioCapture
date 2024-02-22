package com.example.biocapture;

import java.util.List;

public class RegisterViewModel {
    private Biousers user;
    private List<Biousers.Permissions> permissions;

    // Getter and setter for user
    public Biousers getUser() {
        return user;
    }

    public void setUser(Biousers user) {
        this.user = user;
    }

    // Getter and setter for permissions
    public List<Biousers.Permissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Biousers.Permissions> permissions) {
        this.permissions = permissions;
    }
}
