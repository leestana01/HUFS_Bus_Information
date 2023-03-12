package com.leestana.hufsbus

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.leestana.hufsbus.databinding.FragmentTab1Binding
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class TabFragment1 : Fragment() {
    private lateinit var binding: FragmentTab1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTab1Binding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nowtime.text = SimpleDateFormat("기준 시간 yyyy-MM-dd HH:mm").format(System.currentTimeMillis())

        val currentTime = LocalTime.now() // 현재 시각

        // Pair 리스트 생성
        val pairs = mutableListOf<Pair<LocalTime, String>>()
        var busNumber =""

        Log.v("파일 로드","로그 작동 여부 확인")
        val file = File(requireContext().filesDir,"bustime.txt")
        while (!file.exists()) {
            Log.v("파일 로드","파일이 없어 불러오는 중...")
        }

        file.forEachLine { line ->
            if (line.isBlank()) {
                // 빈 문자열인 경우 처리하지 않음
                return@forEachLine
            }
            if (line.startsWith("[")) {
                busNumber = line.substring(line.indexOf("[") + 1, line.indexOf("]"))
            } else {
                val time = LocalTime.parse(line)
                if (time > currentTime) {
                    pairs.add(Pair(time, busNumber))
                }
            }
        }

        // 시간 기준으로 정렬
        pairs.sortBy { it.first }

        // 결과 출력
        val tableLayout = binding.tableLayout

        pairs.forEach { pair ->
            val row = TableRow(requireContext())

            // 버스이미지 생성
            val busImageView = ImageView(requireContext())
            if (pair.second.startsWith("#")){
                busImageView.setImageResource(R.drawable.redbus)
            } else {
                busImageView.setImageResource(R.drawable.bluebus)
            }
            row.addView(busImageView)

            // 버스번호 텍스트뷰 생성
            val busNumberTextView = TextView(requireContext())
            busNumberTextView.text = pair.second
            busNumberTextView.textSize = 18f
            busNumberTextView.gravity = Gravity.CENTER
            row.addView(busNumberTextView)

            // 시간 텍스트뷰 생성
            val timeTextView = TextView(requireContext())
            timeTextView.text = pair.first.format(DateTimeFormatter.ofPattern("HH:mm"))
            timeTextView.textSize = 18f
            timeTextView.gravity = Gravity.CENTER
            row.addView(timeTextView)

            // TableRow를 TableLayout에 추가
            tableLayout.addView(row)
        }
    }
}