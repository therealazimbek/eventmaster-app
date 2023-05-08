let modal = document.querySelector(".modal");

function joinByCode() {
    console.log(modal);
    modal.style.display = "flex";
}

function addVendor() {
    console.log(modal);
    modal.style.display = "flex";
}

function close() {
    modal.style.display = "none";
}


document.addEventListener(
    "click",
    function(event) {
        // If user either clicks X button OR clicks outside the modal window, then close modal by calling closeModal()
        if (
            event.target.matches(".btn") ||
            !event.target.closest(".modal")
        ) {
            close()
        }
    },
    false
)