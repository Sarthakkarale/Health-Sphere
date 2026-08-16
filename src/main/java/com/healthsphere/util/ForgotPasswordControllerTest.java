package com.healthsphere.util;

import com.healthsphere.controller.authentication.ForgotPasswordController;
import com.healthsphere.exceptions.AuthenticationException;

public class ForgotPasswordControllerTest {

    public static void main(String[] args) {

        try {

            ForgotPasswordController controller =
                    new ForgotPasswordController();

            controller.sendPasswordResetEmail(
                    "sarthakkarale7@gmail.com"
            );

            System.out.println(
                    "Password reset email request successful!"
            );

        } catch (AuthenticationException e) {

            System.out.println(
                    "Password reset failed: "
                            + e.getMessage()
            );

        } catch (Exception e) {

            System.out.println(
                    "Unexpected error:"
            );

            e.printStackTrace();
        }
    }
}