document.addEventListener("DOMContentLoaded", function () {
    const textInput = document.getElementById("userName");
    const passwordInput = document.getElementById("phone");
    const loginButton = document.getElementById("loginBtn");

    function checkInputs() {
        const textValue = textInput.value.trim();
        const passwordValue = passwordInput.value.trim();

        if (textValue !== "" && passwordValue !== "") {
            loginButton.disabled = false;

        } else {
            loginButton.disabled = true;

        }
    }

    textInput.addEventListener("input", checkInputs);
    passwordInput.addEventListener("input", checkInputs);
});
