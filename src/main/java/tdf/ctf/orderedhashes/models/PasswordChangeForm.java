package tdf.ctf.orderedhashes.models;

import jakarta.validation.constraints.Size;

public class PasswordChangeForm {
    @Size(min = 8, max = 64)
    private String password;

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
