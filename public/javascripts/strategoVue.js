
$(document).ready(function () {

    var strategoGame = new Vue({
        el:'#stratego-game'
    })

})



Vue.component('stratego-field', {
    template:`
        <div class="gameboard__container" id="board">
        </div>
    `,
});

Vue.component('stratego-info', {
    template:`
        <div class="info__container">
            <p class="info__turn" id="infoPlayer"></p>
        </div>
    `,
});
