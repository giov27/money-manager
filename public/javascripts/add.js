
$( document ).ready(function() {
    getCategoryList()
});

const addButton = () => {
    const date = $('#dateAddForm').val();
    const type = $('#typeAddForm').find(":selected").val();
    const title = $('#titleAddForm').val();
    const category_data = $('#categoryIdAddForm').val() ? parseInt($('#categoryIdAddForm').val()) : $('#categoryAddForm').val();
    const amount = $('#amountAddForm').val();
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
            console.log(response)
            window.location.href = "/";
        }
    })
}

const getCategoryList = () => {
    const categoryVal = (value) =>{
        $('input[id="categoryAddForm"]').val(value)
    }
    const buttonPill = (val) => {
        const button = `<button id=${val.category_id} class="col-3 btn btn-sm bg-secondary rounded-pill text-white buttonPill">
            <span class="pillText">${val.name}</span>
        </button>`
        return button
    }
    $.ajax({
        url: `api/v1/category-list`,
        type: 'GET',
        success: function(response){
            const { category_data } = response.res
            console.log(category_data)
            if(category_data.length > 0 ){
                $('#categoryAddForm').after(
                    '<div class="row gap-2" id="buttonsPill"></div>'
                )
                category_data.forEach((item, i)=>{
                    console.log(item)
                    $('#buttonsPill').append(
                        buttonPill(item)
                    )
                })
                $('.buttonPill').click((e)=>{
                    $('input[id="categoryAddForm"]').val(e.target.innerText)
                    $('input[id="categoryIdAddForm"]').val(e.currentTarget.id)
                })
            }
        }
    })
}

const closeButton = ()=> {
    window.location.href = "/";
}