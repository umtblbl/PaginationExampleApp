package com.umit.paginationexampleapp.data


import android.os.Handler
import android.os.Looper
import com.umit.paginationexampleapp.model.FetchError
import com.umit.paginationexampleapp.model.FetchResponse
import com.umit.paginationexampleapp.model.Person
import com.umit.paginationexampleapp.model.ProcessResult
import com.umit.paginationexampleapp.shared.Constants
import com.umit.paginationexampleapp.shared.utilities.PeopleGenUtility
import com.umit.paginationexampleapp.shared.utilities.RandomUtility
import kotlin.collections.ArrayList
import kotlin.math.min

typealias FetchCompletionHandler = (FetchResponse?, FetchError?) -> Unit

class DataSource {

    companion object {
        private var people: List<Person> = listOf()
    }

    init {
        initializeData()
    }

    fun fetch(next: String?, completionHandler: FetchCompletionHandler) {
        val processResult = processRequest(next)

        Handler(Looper.getMainLooper()).postDelayed({
            completionHandler(processResult.fetchResponse, processResult.fetchError)
        },(processResult.waitTime * 1000).toLong())
    }

    private fun initializeData() {
        if (people.isNotEmpty()) {
            return
        }
        val newPeople: ArrayList<Person> = arrayListOf()
        val peopleCount: Int = RandomUtility.generateRandomInt(range = Constants.DataSource.peopleCountRange)
        for (index in 0 until peopleCount) {
            val person = Person(id = index + 1, fullName = PeopleGenUtility.generateRandomFullName())
            newPeople.add(person)
        }
        people = newPeople.shuffled()
    }

    private fun processRequest(next: String?): ProcessResult {
        var error: FetchError? = null
        var response: FetchResponse? = null
        val isError = RandomUtility.roll(probability = Constants.DataSource.errorProbability)
        val waitTime: Double
        if (isError) {
            waitTime = RandomUtility.generateRandomDouble(range = Constants.DataSource.lowWaitTimeRange)
            error = FetchError(errorDescription = "Internal Server Error")
        } else {
            waitTime = RandomUtility.generateRandomDouble(range = Constants.DataSource.highWaitTimeRange)
            val fetchCount = RandomUtility.generateRandomInt(range = Constants.DataSource.fetchCountRange)
            val peopleCount = people.size
            val nextIntValue = try {
                next!!.toInt()
            } catch (ex: Exception) {
                null
            }
            if (next != null && (nextIntValue == null || nextIntValue < 0)) {
                error = FetchError(errorDescription = "Parameter error")
            } else {
                val endIndex: Int = min(peopleCount, fetchCount + (nextIntValue ?: 0))
                val beginIndex: Int = if (next == null) 0 else min(nextIntValue!!, endIndex)
                var responseNext: String? = if (endIndex >= peopleCount) null else endIndex.toString()
                var fetchedPeople: ArrayList<Person> = ArrayList(people.subList(beginIndex, endIndex)) // begin ile end ayni olunca bos donuyor mu?
                if (beginIndex > 0 && RandomUtility.roll(probability = Constants.DataSource.backendBugTriggerProbability)) {
                    fetchedPeople.add(0, people[beginIndex - 1])
                } else if (beginIndex == 0 && RandomUtility.roll(probability = Constants.DataSource.emptyFirstResultsProbability)) {
                    fetchedPeople = arrayListOf()
                    responseNext = null
                }
                response = FetchResponse(people = fetchedPeople, next = responseNext)
            }
        }
        return ProcessResult(response, error, waitTime)
    }
}
