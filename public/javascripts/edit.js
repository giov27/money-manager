$( document ).ready(function() {
    getLedgerDataById()
});

const months = ["01","02","03","04","05","06","07","08","09","10","11","12"]
var pathname = window.location.href;
var url = new URL(pathname);
var ledger_id = url.searchParams.get("id");

const dateFormatAPI = (chosenDate) => {
    const date = chosenDate.getDate();
    const month = months[chosenDate.getMonth()];
    const year = chosenDate.getFullYear();

    const format = `${year}-${month}-${date}`
    return format
}

getLedgerDataById = ()=>{
    $.ajax({
            url: `api/v1/ledger/${ledger_id}`,
            type: 'get',
            success: function(response){
                const { ledger_data } = response.res
                dateDb = ledger_data.transaction_date.split("/")
                date = new Date(`${dateDb[1]}/${dateDb[0]}/20${dateDb[2]}`)
                $('#dateEditForm').val(dateFormatAPI(date));
                $('#typeEditForm').val(ledger_data.transaction_type).change();
                $('#titleEditForm').val(ledger_data.title);
                $('#categoryEditForm').val(ledger_data.category.name);
                $('#amountEditForm').val(ledger_data.amount);
                $('#noteEditForm').val(ledger_data.note);
            }
        })
}

const closeButton = ()=> {
    window.location.href = "/ledger";
}

const deleteButton = ()=> {
    $.ajax({
        url: `/api/v1/ledger-delete/${ledger_id}`,
        type: 'DELETE',
        success: function(response){
            window.location.href = "/ledger";
        }
    })
}

const editButton = ()=> {
    const date = $('#dateEditForm').val();
    const type = $('#typeEditForm').find(":selected").val();
    const title = $('#titleEditForm').val();
    const category_data = $('#categoryIdEditForm').val() ? parseInt($('#categoryIdEditForm').val()) : $('#categoryEditForm').val();
    const amount = $('#amountEditForm').val();
    const note = $('#noteEditForm').val();
    var json = {
       "transaction_date": date,
       "transaction_type": type,
       "category_id" : category_data,
       "title": title,
       "amount": amount,
       "note": note
    }
    console.log(json)
    $.ajax({
        url: `/api/v1/ledger-update/${ledger_id}`,
        type: 'PUT',
        data: JSON.stringify(json),
        contentType: 'application/json; charset=utf-8',
        cache: false,
        dataType: "json",
        success: function(response){
            window.location.href = "/ledger";
        }
    })
}