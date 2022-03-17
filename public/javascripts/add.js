function numberWithCommas(x) {
    return x.toString().replace(/\B(?<!\,\d*)(?=(\d{3})+(?!\d))/g, ".");
}

$( document ).ready(function() {
    $('#formAddSubmit').submit((e)=>{
        e.preventDefault();
        addButton()
    })
    $('#amountAddForm').keyup((e)=>{
        $('#amountAddForm').val(formatRupiah(e.target.value, 'Rp. '));
    })
//    $('#amountAddForm').val().replace(/\B(?<!\,\d*)(?=(\d{3})+(?!\d))/g, ".")
});

//var rupiah_currency = document.getElementById('amountAddForm');
//rupiah_currency.addEventListener('keyup', function(e)
//{
//    rupiah_currency.value = formatRupiah(this.value, 'Rp. ');
//});

const formatRupiah = (number, prefix) =>
    {
        var number_string = number.replace(/[^,\d]/g, '').toString(),
            split    = number_string.split(','),
            rest     = split[0].length % 3,
            rupiah   = split[0].substr(0, rest),
            thousand = split[0].substr(rest).match(/\d{3}/gi);

        if (thousand) {
            separator = rest ? '.' : '';
            rupiah += separator + thousand.join('.');
        }

        rupiah = split[1] != undefined ? rupiah + ',' + split[1] : rupiah;
        return prefix == undefined ? rupiah : (rupiah ? 'Rp. ' + rupiah : '');

    }

const addButton = () => {
    console.log("hit")
    const date = $('#dateAddForm').val();
    const type = $('#typeAddForm').find(":selected").val();
    const title = $('#titleAddForm').val();
    const category_data = $('#categoryIdAddForm').val() ? parseInt($('#categoryIdAddForm').val()) : $('#categoryAddForm').val();
    const amount = $('#amountAddForm').val().replace(/\D/g,'');
    const note = $('#noteAddForm').val();
    var json = {
       "transaction_date": date,
       "transaction_type": type,
       "category_id" : category_data,
       "title": title,
       "amount": amount,
       "note": note
    }
    $.ajax({
        url: `api/v1/ledger-create`,
        type: 'POST',
        data: JSON.stringify(json),
        contentType: 'application/json; charset=utf-8',
        cache: false,
        dataType: "json",
        success: function(response){
            window.location.href = "/";
        }
    })
}

const getCategoryChange = () => {
    $('#buttonsPill').remove();
    $('input[id="categoryIdAddForm"]').val('')
    console.log($('input[id="categoryIdAddForm"]').val())
    const formInput = $('#categoryAddForm').val()
    const value = formInput.charAt(0).toUpperCase() + formInput.slice(1);

    const buttonPill = (val) => {
        const button = `<button id=${val.category_id} class="col-4 btn btn-sm bg-secondary rounded-pill text-white buttonPill">
            <span class="pillText">${val.name}</span>
        </button>`
        return button
    }
    if(value != ''){
        $.ajax({
            url: `api/v1/category-list-by-search?name=${value}`,
            type: 'GET',
            success: function(response){
                const { category_data } = response.res
                if(category_data.length > 0 ){
                    $('#categoryAddForm').after(
                        '<div class="row gap-2" id="buttonsPill"></div>'
                    )
                    category_data.forEach((item, i)=>{
                        $('#buttonsPill').append(
                            buttonPill(item)
                        )
                    })
                    $('.buttonPill').click((e)=>{
                        e.preventDefault();
                        $('input[id="categoryAddForm"]').val(e.target.innerText)
                        $('input[id="categoryIdAddForm"]').val(e.currentTarget.id)
                        console.log($('input[id="categoryIdAddForm"]').val())
                    })
                }
            }
        })
    }
}

const closeButton = ()=> {
    window.location.href = "/";
}