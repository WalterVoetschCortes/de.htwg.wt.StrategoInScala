window.addEventListener("keydown", checkKeyPress, false)

let size = 0
let colA = 0
let rowA = 0
let rowD = 0
let colD = 0
let attacking = false

class MatchField {

    constructor() {
        this.fields = []
    }

    fill(newFields) {
        this.fields = newFields
    }

    createView () {
        let html = ""
        for(let row = 0; row < size; row++) {
            html += '<tr>'
            for(let col = 0; col < size; col++) {
            if(this.fields.colour === 0) {
                    html += '<td class="cells_blue cell">'
                    switch (this.fields.figName) {
                        case 'F':
                            html += '<span> <img class="piece" src="@routes.Assets.versioned("images/character-flag.svg")" alt="1"/> </span>'
                            break;

                        case 'B':
                            html += '<span> <img class="piece" src="@routes.Assets.versioned("images/character-bomb.svg")" alt="1"/> </span>'
                            break;

                        case 'M':
                            html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-marshal.svg")" alt="1"/> </span>'
                            break;
                        case '1':
                            html+='<span> <img class="piece" src="@routes.Assets.versioned("images/character-spy.svg")" alt="1"/> </span>'
                            break;
                        case '2':
                            html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-scout.svg")" alt="1"/> </span>'
                            break;
                        case '3':
                            html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-miner.svg")" alt="1"/> </span>'
                            break;
                        case '4':
                            html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-sergeant.svg")" alt="1"/> </span>'
                            break;
                        case '5':
                            html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-lieutenant.svg")" alt="1"/> </span>'
                            break;
                        case '6':
                            html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-captain.svg")" alt="1"/> </span>'
                            break;
                        case '7':
                            html+= 'span> <img class="piece" src="@routes.Assets.versioned("images/character-major.svg")" alt="1"/> </span>'
                            break;
                        case "8":
                            html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-colonel.svg")" alt="1"/> </span>'
                            break;
                        case "9":
                            html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-general.svg")" alt="1"/> </span>'
                            break;
                        }
                        html += '</td>'
            } else if (this.fields.colour === 1) {
                html += '<td class="cells_red cell">'
                switch (this.fields.figName) {
                    case 'F':
                        html += '<span> <img class="piece" src="@routes.Assets.versioned("images/character-flag.svg")" alt="1"/> </span>'
                        break;

                    case 'B':
                        html += '<span> <img class="piece" src="@routes.Assets.versioned("images/character-bomb.svg")" alt="1"/> </span>'
                        break;

                    case 'M':
                        html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-marshal.svg")" alt="1"/> </span>'
                        break;
                    case '1':
                        html+='<span> <img class="piece" src="@routes.Assets.versioned("images/character-spy.svg")" alt="1"/> </span>'
                        break;
                    case '2':
                        html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-scout.svg")" alt="1"/> </span>'
                        break;
                    case '3':
                        html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-miner.svg")" alt="1"/> </span>'
                        break;
                    case '4':
                        html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-sergeant.svg")" alt="1"/> </span>'
                        break;
                    case '5':
                        html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-lieutenant.svg")" alt="1"/> </span>'
                        break;
                    case '6':
                        html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-captain.svg")" alt="1"/> </span>'
                        break;
                    case '7':
                        html+= 'span> <img class="piece" src="@routes.Assets.versioned("images/character-major.svg")" alt="1"/> </span>'
                        break;
                    case "8":
                        html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-colonel.svg")" alt="1"/> </span>'
                        break;
                    case "9":
                        html+= '<span> <img class="piece" src="@routes.Assets.versioned("images/character-general.svg")" alt="1"/> </span>'
                        break;
                }
                html += '</td>'
                } else {
                html += '<td class="cells__green cell"> <span class="cell__empty"> </span> </td>'
                }
            }
        html += '</tr>'
        }
        html += '</table>'
        return html
    }

    updateView() {
        const html = this.createView()
        $("#board").html(html)
    }

    /**
     * registers the click listener for every single cell
     */
    registerClickListener() {
        for (let grid = 0; grid < this.grids.length; grid++) {
            for (let row = 0; row < this.grids[grid].length; row++) {
                for (let column = 0; column < this.grids[grid][row].length; column++) {
                    if (this.grids[grid][row][column] === '-') {
                        $(document).on('click', '#notSet' + grid + '-' + row + '-' + column, () => {
                            this.move(grid, row, column)
                        })
                    }
                }
            }
        }
    }

}

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



function loadJson() {
    $.ajax({
        method: "GET",
        url: "/json",
        dataType: "json",

        success: function (result) {
            matchField = new MatchField();
            matchField.fill(result.matchField);
            updateMatchField(matchField);
            registerClickListener();
        }
    });
}

$( document ).ready(function() {
    console.log( "Document is ready, filling matchfield" );
    loadJson();
});
