package com.example.lf_wannabe.mvvm_example.calendar

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.lf_wannabe.mvvm_example.R
import android.widget.TextView
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.GridView
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.layout_calendar.view.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by lf_wannabe on 18/09/2017.
 */
class CustomCalendarView : LinearLayout{

    constructor(context: Context, attrs: AttributeSet, defStyleAttrs: Int)
            : super(context, attrs, defStyleAttrs)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    constructor(context: Context) : super(context)

    // date format
    private var dateFormat: String? = null

    // current displayed month
    private val currentDate = Calendar.getInstance()

    //event handling
    private var eventHandler: EventHandler? = null

    init {
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.layout_calendar, this)

        updateCalendar()
    }

//    private fun loadDateFormat(attrs: AttributeSet) {
//        val ta = context.obtainStyledAttributes(attrs, R.styleable.CalendarView)
//
//        try {
//            // try to load provided date format, and fallback to default otherwise
//            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat)
//            if (dateFormat == null)
//                dateFormat = DATE_FORMAT
//        } finally {
//            ta.recycle()
//        }
//    }

    /**
     * Display dates correctly in grid
     */
    @JvmOverloads
    fun updateCalendar(events: HashSet<Date>? = null) {
        val cells = ArrayList<Date>()
        val calendar = currentDate.clone() as Calendar

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        // fill cells
        while (cells.size < DAYS_COUNT) {
            cells.add(calendar.getTime())
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // update grid
        calendar_grid!!.adapter = CalendarAdapter(context, cells, events)

        // update title
//        val sdf = SimpleDateFormat(dateFormat)
//        txtDate!!.setText(sdf.format(currentDate.getTime()))

        // set header color according to current season
//        val month = currentDate.get(Calendar.MONTH)
//        val season = monthSeason[month]
    }


    private inner class CalendarAdapter(context: Context, days: ArrayList<Date>,
                                        private val eventDays: HashSet<Date>?)
        : ArrayAdapter<Date>(context, R.layout.layout_calendar_day, days) {

        // for view inflation
        private val inflater: LayoutInflater

        init {
            inflater = LayoutInflater.from(context)
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup): View {
            var view = view
            // day in question
            val date = getItem(position)
            val day = date!!.date
            val month = date!!.getMonth()
            val year = date!!.getYear()

            // today
            val today = Date()

            // inflate item if it does not exist yet
            if (view == null)
                view = inflater.inflate(R.layout.layout_calendar_day, parent, false)

            // if this day has an event, specify event image
            view!!.setBackgroundResource(0)
            if (eventDays != null) {
                for (eventDate in eventDays) {
                    if (eventDate.getDate() === day &&
                            eventDate.getMonth() === month &&
                            eventDate.getYear() === year) {
                        // mark this day for event
                        break
                    }
                }
            }

            // clear styling
            (view as TextView).setTypeface(null, Typeface.NORMAL)
            (view as TextView).setTextColor(Color.BLACK)

            if (month != today.getMonth() || year != today.getYear()) {
                // if this day is outside current month, grey it out
                (view as TextView).setTextColor(resources.getColor(R.color.greyed_out))
            } else if (day == today.getDate()) {
                // if it is today, set it to blue/bold
                (view as TextView).setTypeface(null, Typeface.BOLD)
                (view as TextView).setTextColor(resources.getColor(R.color.today))
            }

            // set text
            (view as TextView).setText(date!!.getDate().toString())

            return view
        }
    }

    /**
     * Assign event handler to be passed needed events
     */
    fun setEventHandler(eventHandler: EventHandler) {
        this.eventHandler = eventHandler
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    interface EventHandler {
        fun onDayLongPress(date: Date)
    }

    companion object {
        // for logging
        private val LOGTAG = "Calendar View"

        // how many days to show, defaults to six weeks, 42 days
        private val DAYS_COUNT = 42

        // default date format
        private val DATE_FORMAT = "MMM yyyy"
    }
}
/**
 * Display dates correctly in grid
 */