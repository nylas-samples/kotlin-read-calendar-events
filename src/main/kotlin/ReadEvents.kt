// Import Nylas packages
import com.nylas.NylasClient
import com.nylas.models.*
// Import DotEnv to handle .env files
import io.github.cdimascio.dotenv.dotenv

// Import Kotlin packages
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun main(args: Array<String>){
    // Load our env variable
    val dotenv = dotenv()

    // Initialize Nylas client
    val nylas: NylasClient = NylasClient(
        apiKey = dotenv["V3_TOKEN"],
        baseUrl = dotenv["BASE_URL"],
    )

    // Get today's day
    var startDate = LocalDateTime.now()
    // Set time. As we're using UTC we need to add the hours in difference
    // from our own Timezone
    startDate = startDate.withHour(12);
    startDate = startDate.withMinute(0);
    startDate = startDate.withSecond(0);
    val endDate = startDate.withHour(21);

    // Set up the query for the list of events
    val eventquery: ListEventQueryParams = ListEventQueryParams(calendarId = dotenv["CALENDAR_ID"],
    start = startDate.toEpochSecond(ZoneOffset.UTC).toString(),
    end = endDate.toEpochSecond(ZoneOffset.UTC).toString())
    // Get a list of events
    val myevents: List<Event> = nylas.events().list(dotenv["CALENDAR_ID"], queryParams = eventquery).data
    // Loop through the events
    for(event in myevents){
        // Print the Id and Title of the event
        print("Id: " + event.id + " | ");
        print("Title: " + event.title);
        // Get the details of Date and Time of each event
        when(event.getWhen().getObject().toString()){
            "DATESPAN" -> {
                val datespan = event.getWhen() as When.Datespan
                print(" | The date of the event is: " + datespan.startDate);
            }
            "TIMESPAN" -> {
                val timespan = event.getWhen() as When.Timespan
                val startDate = Date(timespan.startTime.toLong() * 1000)
                val endDate = Date(timespan.endTime.toLong() * 1000)
                print(" | The time of the event is from: $startDate to $endDate");
            }
        }
        print(" | Participants: ");
        // Get a list of the event participants
        val participants = event.participants
        // Loop through and print their email, name and status
        for(participant in participants){
            print(" Email: " + participant.name + " Name: " + participant.email + " Status: " + participant.status)
        }
        println("\n")
    }
}
