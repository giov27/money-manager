var today = new Date();
const days = ["Sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat"];
const months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
const months2 = ["01","02","03","04","05","06","07","08","09","10","11","12"]
let income, expense, balance = 0

$( document ).ready(function() {
    $('.date').html(dateFormatUI(today));
    countData(today)
    ledgerData(today)
});

const nextDay = () => {
    today.setDate(today.getDate() + 1)
    $('.date').html(dateFormatUI(today));
    countData(today)
    ledgerData(today)
}
const previousDay = () => {
    today.setDate(today.getDate() - 1)
    $('.date').html(dateFormatUI(today));
    countData(today)
    ledgerData(today)
}

const dateFormatUI = (chosenDate) => {
    const day = days[chosenDate.getDay()];
    const date = chosenDate.getDate();
    const month = months[chosenDate.getMonth()];
    const year = chosenDate.getFullYear();

    const format = `${day}, ${month} ${date} ${year}`
    return format
}

const dateFormatAPI = (chosenDate) => {
    const date = chosenDate.getDate();
    const month = months2[chosenDate.getMonth()];
    const year = chosenDate.getFullYear();

    const format = `${year}-${month}-${date} `
    return format
}

const countData = (date) => {
    $.ajax({
        url: `api/v1/income-by-date?date=${dateFormatAPI(date)}`,
        type: 'get',
        success: function(response){
            const {income_data} = response.res
            income = income_data
            $('.income').html(`Rp ${income_data}`)
        }
    }).done(()=>{
        $.ajax({
            url: `api/v1/expense-by-date?date=${dateFormatAPI(date)}`,
            type: 'get',
            success: function(response){
                const {expense_data} = response.res
                expense = expense_data
                balance = income - expense
                $('.expense').html(`Rp ${expense_data}`)
                $('.balanceCurrent').html(`Rp ${balance}`)
            }
        })}
    )
}

const dateMonthly = ()=> {
    var firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
    var lastDay = new Date(today.getFullYear(), today.getMonth() + 1, 0);
    var json = {
        "firstDay" = firstDay,
        "lastDay" = lastDay
    }
    $.ajax({
            url: `api/v1/`,
            type: 'POST',
            data: JSON.stringify(json),
            contentType: 'application/json; charset=utf-8',
            cache: false,
            dataType: "json",
            success: function(response){
                console.log(response)
//                window.location.href = "/ledger";
            }
        })
}