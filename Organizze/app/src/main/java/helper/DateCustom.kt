package helper

import java.text.SimpleDateFormat

class DateCustom {

    companion object {
        fun getCurrentDateFormatted(): String {
            val dateLong: Long = System.currentTimeMillis()
            val formatterDate: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            return formatterDate.format(dateLong)
        }

        fun getOnlyMonthAndYear(date: String): String{
            val list = date.split("/").subList(1, 3).toList()
            return list[0]+list[1]
        }
    }

}