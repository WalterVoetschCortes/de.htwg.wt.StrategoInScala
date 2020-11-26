window.addEventListener("keydown", checkKeyPress, false)

let colA = 0
let rowA = 0
let rowD = 0
let colD = 0
let attacking = false

$('.cell').click(function () {
    if (attacking === false) {
        colA = this.parentElement.rowIndex
        rowA = this.cellIndex
    } else {
        colD = this.parentElement.rowIndex
        rowD = this.cellIndex
        window.location = "/attack/" + colA + "/" + rowA + "/" + colD + "/" + rowD
        attacking = false
    }
})


function checkKeyPress(key) {
    attacking = key.keyCode === "65";
    if (key.keyCode === "37") {
        dir = "l"
    } else if (key.keyCode === "38") {
        dir = "u"
    } else if (key.keyCode === "39") {
        dir = "r"
    } else if (key.keyCode === "40") {
        dir = "d"
    }
    window.location = "/move/" + dir + "/" + colA + "/" + rowA
}
