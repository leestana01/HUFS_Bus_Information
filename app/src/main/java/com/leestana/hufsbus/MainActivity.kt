package com.leestana.hufsbus

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.leestana.hufsbus.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //버스 목록표 불러오기
        loadTimeTable()
        //탭 레이아웃 제작
        setTabLayout()
    }

    private fun loadTimeTable(){
        try{
            lifecycleScope.launch { //인터넷에서 버스 목록표 불러오기
                val result = downloadTimeTable(this@MainActivity)
                val updateCheck = checkUpdate()
                val noticeCheck = getNotice(this@MainActivity)
                if (result) {
                    if (updateCheck){ Toast.makeText(this@MainActivity, "<<새 버전이 존재합니다!>>\n\n\n새로운 버전을 다운받아주세요!", Toast.LENGTH_SHORT).show()
                    } else { Toast.makeText(this@MainActivity, "시간표를 불러오는데 성공했습니다", Toast.LENGTH_SHORT).show() }
                    if(!noticeCheck){copyNotice()}
                } else {
                    copyTimeTable() // 내장된 목록표 불러오기
                    copyNotice()
                    Toast.makeText(this@MainActivity, "<<서버 통신 실패>>\n내장된 시간표를 불러옵니다\n\n내장된 시간표는 오차가 발생할 수 있습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
        catch (e:Exception){
            Toast.makeText(this@MainActivity, "심각한 에러 발생", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun downloadTimeTable(context: Context) = withContext(Dispatchers.IO) {
        try {
            val url : String = when(nowDay()){
                1 -> "https://gist.githubusercontent.com/leestana01/c81a80308d31759040b19177539ca0e8/raw/HUFSBUS_bustime_sunday.txt"
                7 -> "https://gist.githubusercontent.com/leestana01/c81a80308d31759040b19177539ca0e8/raw/HUFSBUS_bustime_saturday.txt"
                else -> "https://gist.githubusercontent.com/leestana01/c81a80308d31759040b19177539ca0e8/raw/HUFSBUS_bustime_weekday.txt"
            }

            // 파일 저장
            val sb = readfile(url)
            val file = File(context.filesDir, "bustime.txt")
            file.writeText(sb.toString())

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun checkUpdate() = withContext(Dispatchers.IO) {
        try {
            val url ="https://gist.githubusercontent.com/leestana01/c81a80308d31759040b19177539ca0e8/raw/HUFSBUS_bustime_appVersion"
            val latestVersion = when(readfile(url).toString()){
                "false" -> "0.0.0"
                else -> readfile(url).toString()
            }

            val versionName = BuildConfig.VERSION_NAME

            latestVersion > versionName

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun getNotice(context: Context) = withContext(Dispatchers.IO) {
        try {
            val url = "https://gist.githubusercontent.com/leestana01/c81a80308d31759040b19177539ca0e8/raw/HUFSBUS_notice"

            // 파일 저장
            val sb = readfile(url)
            val file = File(context.filesDir, "notice.txt")
            file.writeText(sb.toString())

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun readfile(fileurl: String) = withContext(Dispatchers.IO){
        try {
            val url = URL(fileurl)
            val connection = url.openConnection()
            connection.connect()
            val input: InputStream = connection.getInputStream()
            val reader = BufferedReader(InputStreamReader(input))
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
                sb.append("\n")
            }
            reader.close()
            if (sb.length > 2) { sb.delete(sb.length - 1, sb.length) }

            sb.toString()

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun copyTimeTable(){
        val inputStream: InputStream = when(nowDay()){
            1 -> resources.openRawResource(R.raw.bustime_sunday)
            7 -> resources.openRawResource(R.raw.bustime_saturday)
            else -> resources.openRawResource(R.raw.bustime_weekdays)
        }
        val text = inputStream.bufferedReader().use(BufferedReader::readText)
        val file = File(filesDir, "bustime.txt")
        file.writeText(text)
    }

    private fun copyNotice(){
        val inputStream = resources.openRawResource(R.raw.notice)
        val text = inputStream.bufferedReader().use(BufferedReader::readText)
        val file = File(filesDir, "notice.txt")

        file.writeText(text)
    }

    //오늘 요일 반환
    private fun nowDay(): Int {
        val cal: Calendar = Calendar.getInstance()
        return cal.get(Calendar.DAY_OF_WEEK)
    }

    private fun setTabLayout(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when (position){
                    0 -> tab.text = resources.getString(R.string.tab_name_1)
                    1 -> tab.text = resources.getString(R.string.tab_name_2)
                    2 -> tab.text = resources.getString(R.string.tab_name_3)
                }
            }.attach()
        }
    }

    override fun onBackPressed() {
        finish()
    }

}

