package shop.sorayeon.android.lotto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    // 초기화버튼
    private val clearButton: Button by lazy {
        findViewById<Button>(R.id.clearButton)
    }

    // 번호추가하기 버튼
    private val addButton: Button by lazy {
        findViewById<Button>(R.id.addButton)
    }

    // 자동생성시작 버튼
    private val runButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.runButton)
    }

    // 넘버피커
    private val numberPicker: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker)
    }

    // 넘버텍스트뷰 (6개 숫자)
    private val numberTextViewList: List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.textView1),
            findViewById<TextView>(R.id.textView2),
            findViewById<TextView>(R.id.textView3),
            findViewById<TextView>(R.id.textView4),
            findViewById<TextView>(R.id.textView5),
            findViewById<TextView>(R.id.textView6)
        )
    }

    // 자동 실행 상태 변수
    private var didRun = false

    // 사용자가 선택한 숫자 SET
    private val pickNumberSet = hashSetOf<Int>()

    // 엑티비티가 생성되면 호출
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // numberPicker 선택 범위 (로또 추첨기에 맞게 1~45)
        numberPicker.minValue = 1
        numberPicker.maxValue = 45

        // 자동생성
        initRunButton()
        // 번호추가
        initAddButton()
        // 번호초기화버튼
        initClearButton()
    }

    // 번호추가
    private fun initAddButton() {
        // 번호추가 버튼 클릭 이벤트 추가
        addButton.setOnClickListener {
            // 유효성 검사 START
            // 이미 자동 생성되었다면 초기화 후 번호선택 가능
            if (didRun) {
                // 토스트 경고 생성
                Toast.makeText(this, "초기화 후에 시도해주세요.", Toast.LENGTH_SHORT).show()
                // 이벤트만 빠져나가기 위한코드
                return@setOnClickListener
            }
            // 총 5개의 번호까지 사용자가 선택할 수 있음 (선택된 숫자 SET 에 번호가 5개 이상있다면 예외)
            if (pickNumberSet.size >= 5) {
                Toast.makeText(this, "번호는 5개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 선택되어 있지 않은 번호만 추가 가능 (선택된 숫자 SET 에 넘버피커에 선택한 숫자가 있으면 예외)
            if (pickNumberSet.contains(numberPicker.value)) {
                Toast.makeText(this, "이미 선택한 번호입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 유효성 검사 END

            // 다음 텍스트 뷰는 선택된 숫자 SET 의 사이즈
            val textView = numberTextViewList[pickNumberSet.size]
            // 숨겨둔 텍스트를 보여준다
            textView.isVisible = true
            // 텍스트는 넘버피커에서 선택한 값
            textView.text = numberPicker.value.toString()
            // 숫자의 배경 변경 (원)
            setNumberBackground(numberPicker.value, textView)

            // 선택된 숫자 SET 에 번호 추가
            pickNumberSet.add(numberPicker.value)
        }
    }

    // 번호 초기화
    private fun initClearButton() {
        // 번호초기화 버튼 클릭 이벤트 추가
        clearButton.setOnClickListener {
            pickNumberSet.clear()
            numberTextViewList.forEach {
                it.isVisible = false
            }
            didRun = false
        }
    }

    // 자동 생성 시작
    private fun initRunButton() {
        // 자동생성 시작버튼 클릭 이벤트
        runButton.setOnClickListener {
            // 숫자 6개를 뽑아오는 함수호출
            val list = getRandomNumber()

            didRun = true

            list.forEachIndexed { index, number ->
                val textView = numberTextViewList[index]

                textView.text = number.toString()
                textView.isVisible = true
                // 숫자의 배경 변경 (원)
                setNumberBackground(number, textView)
            }

            Log.d("MainActivity", list.toString())
        }
    }

    // 생성된 숫자에 배경을 드로어블 모형으로 배경을 만들어주는 함수
    private fun setNumberBackground(number: Int, textView: TextView) {
        // 숫자에 따라 원의 배경색을 바꿔준다
        when(number) {
            // 1~10까지는 노란색 배경
            in 1..10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yellow)
            in 11..20 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
            in 21..30 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
            in 31..40 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_gray)
            else -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
        }
    }

    // 총 6개의 숫자 리스트 반환 (선택한 숫자 + 랜덤 숫자)
    private fun getRandomNumber(): List<Int> {

        // mutable 리스트 생성 및 초기화 (추가 add, 랜덤으로 섞어주는 shuffle 생성한 리스트 변경이 필요)
        val numberList = mutableListOf<Int>()
            // 초기화
            .apply {
                // 1~45 개의 숫자를 준비하는데 이미 선택한 숫자는 제외한다
                // 4개의 숫자가 선택되어있다면 4개의 숫자를 제외한 41개의 숫자만 numberList 에 담긴다.
                for (i in 1..45) {
                    if (pickNumberSet.contains(i)) {
                        continue
                    }
                    this.add(i)
                }
            }

        // 리스트를 랜덤하게 섞어주는 함수
        numberList.shuffle()

        // 선택한 숫자 리스트는 고정 (set 을 list 로 변환) +
        // 랜덤으로 잘섞어준 숫자가 선택한 숫자포함 총 6개가 되도록 잘라서 반환 (6개 - 선택한 숫자리스트 사이즈)
        val newList = pickNumberSet.toList() + numberList.subList(0, 6 - pickNumberSet.size)
        // 순서대로 보일 수 있도록 정렬하여 리턴
        return newList.sorted()
    }


}