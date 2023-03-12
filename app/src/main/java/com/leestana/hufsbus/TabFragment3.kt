package com.leestana.hufsbus

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.leestana.hufsbus.databinding.FragmentTab3Binding
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class TabFragment3 : Fragment() {
    private lateinit var binding: FragmentTab3Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTab3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.notice.text = "공지사항을 불러오는데 실패하였습니다."

        setNotice()
    }

    private fun setNotice(){
        Log.v("파일 로드","로그 작동 여부 확인")
        val file = File(requireContext().filesDir,"notice.txt")
        while (!file.exists()) {
            Log.v("파일 로드","파일이 없어 불러오는 중...")
        }

        val reader = BufferedReader(FileReader(file))
        val stringBuilder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line).append('\n')
        }
        reader.close()
        val noticeText = stringBuilder.toString()
        binding.notice.text = noticeText
    }


}