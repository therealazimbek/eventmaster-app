let modal = document.querySelector(".modal");
let buyModal = document.querySelector(".buyModal");
    let addGuestModal = document.querySelector(".addGuestModal");

function joinByCode() {
    console.log(modal);
    modal.style.display = "flex";
}

function buyForm() {
    buyModal.style.display = "flex";
}

function addGuests() {
    addGuestModal.style.display = "flex";
}

function closeAddGuestModal() {
    addGuestModal.style.display = "none";
}

function closeBuyForm() {
    buyModal.style.display = "none";
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

document.addEventListener(
    "click",
    function(event) {
        // If user either clicks X button OR clicks outside the modal window, then close modal by calling closeModal()
        if (
            event.target.matches(".btn") ||
            !event.target.closest(".buyModal")
        ) {
            closeBuyForm()
        }
    },
    false
)

document.addEventListener(
    "click",
    function(event) {
        // If user either clicks X button OR clicks outside the modal window, then close modal by calling closeModal()
        if (
            event.target.matches(".btn") ||
            !event.target.closest(".addGuestModal")
        ) {
            closeAddGuestModal()
        }
    },
    false
)