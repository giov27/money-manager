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
//        async: false,
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

const ledgerData = (date) => {
    $('.transactionDetail').empty()
    $.ajax({
        url: `api/v1/ledger-list-by-date?date=${dateFormatAPI(date)}`,
        type: 'get',
        success: function(response){
            const { ledger_data } = response.res
            console.log(ledger_data)
            $.each(ledger_data, function(res1, res2){
                $('.transactionDetail').append(
                    `<a href="/edit-ledger?id=${res2.ledger_id}" class="text-white text-decoration-none"><div class="d-flex justify-content-between borderBottom">

                        <div class="d-flex">
                             <i class="${res2.category.icon} transactionIcon"></i>
                             <div class="my-auto ms-3">
                                 <p class="profileName my-auto"> ${res2.title} <br> <span class="profileDesc text-capitalize">${res2.category.name} / ${res2.transaction_type}</span></p>
                             </div>
                         </div>
                         <div class="d-flex align-items-center">
                             <h5>Rp ${res2.amount}</h5>
                         </div>

                     </div></a>`
                )
            })
        }
    })
}