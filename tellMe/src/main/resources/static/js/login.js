document.addEventListener("DOMContentLoaded", function () {
    const textInput = document.querySelector("input[type='text']");
    const passwordInput = document.querySelector("input[type='password']");
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