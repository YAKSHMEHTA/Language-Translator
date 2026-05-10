async function translateText() {

    const response = await fetch("http://localhost:8080/translate", {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({
            text: "wht is ur name",
            target: "hi-IN"
        })
    });

    const data = await response.json();

    console.log(data);

    const ress = document.querySelector(".ress");

    ress.textContent = data.translatedText;
}

translateText();