package com.leestana.hufsbus

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.tabs.TabLayoutMediator
import com.leestana.hufsbus.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        try{ //인터넷에서 버스 목록표 불러오기
            throw java.lang.Exception()
        }
        catch (e:Exception){
            try{ //실패 시 내장된 목록표 불러오기
                copyTimeTable()
                Toast.makeText(this@MainActivity, "시간표를 불러오는데 성공했습니다", Toast.LENGTH_SHORT).show()
            }
            catch (e:Exception){
                Toast.makeText(this@MainActivity, "심각한 에러 발생", Toast.LENGTH_SHORT).show()
            }
        }



        // 하기 내용 주의!
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

    private fun copyTimeTable(){
        val cal: Calendar = Calendar.getInstance()
        val inputStream: InputStream = when(cal.get(Calendar.DAY_OF_WEEK)){
            1 -> resources.openRawResource(R.raw.bustime_sunday)
            7 -> resources.openRawResource(R.raw.bustime_saturday)
            else -> resources.openRawResource(R.raw.bustime_weekdays)
        }
        val text = inputStream.bufferedReader().use(BufferedReader::readText)

        val file = File(filesDir, "bustime.txt")
        file.writeText(text)
    }

    override fun onBackPressed() {
        finish()
    }

}

