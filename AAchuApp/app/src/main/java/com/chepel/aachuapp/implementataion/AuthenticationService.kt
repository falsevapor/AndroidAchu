package com.chepel.aachuapp.implementataion

import com.chepel.aachuapp.interfaces.IAuthenticationService
import com.chepel.aachuapp.interfaces.User
import com.google.android.gms.tasks.Task
import com.google.gson.JsonObject
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject
import cz.msebera.android.httpclient.protocol.HTTP
import cz.msebera.android.httpclient.message.BasicHeader
import cz.msebera.android.httpclient.entity.StringEntity








/**
 * Created by maksim.chepel on 12/6/17.
 */
class AzureAuthenticationService: IAuthenticationService {

    class CompletionHandler (OnGood: ()->Unit, OnError: ()->Unit)
    {
        val onGood = OnGood
        val onError = OnError
    }
    var RequestTimeout: Int = 5000
    var ConnectTimeout: Int = 5000

    val AchuAzureURL: String = "https://datapultapi.azurewebsites.net/api"
/*
    fun getClient(): AsyncHttpClient
    {
        return getClient(ConnectTimeout, RequestTimeout)
    }

    fun getClient(connectionTimeoutMs: Int, requestTimeoutMs: Int): AsyncHttpClient
    {
        return asyncHttpClient(config().setConnectTimeout(connectionTimeoutMs).setRequestTimeout(requestTimeoutMs))
    }
*/
    fun login(user:User, resultHandler: CompletionHandler)
    {
        try
        {
            val client = AsyncHttpClient()

            client.addHeader("ZUMO-API-VERSION", "2.0.0")
            client.addHeader("Content-Type", "application/json")
            client.addHeader("Accept", "application/json")
            /*val params = RequestParams()
            params.put("Username", user.ID)
            params.put("Password", user.Password)
*/
            val j = JsonObject()
            j.addProperty("Username", user.ID)
            j.addProperty("Password", user.Password)

            val entity = StringEntity(j.toString())
            entity.contentType = BasicHeader(HTTP.CONTENT_TYPE, "application/json")

            /*
            val j:JsonObject = JsonObject()
            j.addProperty("Username", user.ID)
            j.addProperty("Password", user.Password)
            */
            val f = client.post(null,"$AchuAzureURL/Auth", entity, "application/json", object: JsonHttpResponseHandler()
            {
                override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                    // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                    // Handle resulting parsed JSON response here
                    resultHandler.onGood()
                }

                override fun onFailure(statusCode: Int, headers: Array<Header>, res: String, t: Throwable) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    resultHandler.onError()
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONArray?) {
                    super.onFailure(statusCode, headers, throwable, errorResponse)
                    resultHandler.onError()
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                    super.onFailure(statusCode, headers, throwable, errorResponse)
                    resultHandler.onError()
                }
            })

        }
        catch (e: Exception)
        {
            resultHandler.onError()
        }
        finally
        {

        }
    }
/*
    override fun AuthenticateEmailAsync(email: String, password: String): Task<IAuthenticationService.AuthenticationResult>
    {
        val result = IAuthenticationService.AuthenticationResult
        try {
            val authenticateUserResponse = _webServiceProvider.AuthenticateEmail(email, password)
            if (authenticateUserResponse.Success) {
                result.Success = true
                val session = authenticateUserResponse.Data
                //Session.Current = session;
                val jsonData = JsonConvert.SerializeObject(session)
                //_settingsProvider.SetString(CurrentSessionKey + email, jsonData);
            } else {
                result.Success = false
                result.Error = IAuthenticationService.AuthenticationError.ServerError
                result.ServerErrorMessage = authenticateUserResponse.Status.ToString() + " " + authenticateUserResponse.ErrorMessage
            }
        } catch (ex: Exception) {
            result.Success = false
            result.Error = IAuthenticationService.AuthenticationError.NetworkError
        }

        return result
    }

    override lateinit var CurrentUser: IUser
    override lateinit var NewUser: IUser
    override fun ResetPasswordAsync(userID: String): Task<IAuthenticationService.AuthenticationResult>
    {
        return null
    }
    override fun AddUserAsync() : Task<IAuthenticationService.AuthenticationResult>
    {
        return null
    }
    */
}