package tdf.ctf.orderedhashes.models;

import jakarta.validation.constraints.Size;

public class PasswordChangeForm {
    @Size(min = 3, max = 64)
    private String password;

    // imagine not asking for old pwd

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
