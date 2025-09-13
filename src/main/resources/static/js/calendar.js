$(function() {
  const $grid = $("#calendarGrid");
  const today = $grid.data("today"); // "2025-09-10"
  const now = new Date(today);

  let currentYear = now.getFullYear();
  let currentMonth = now.getMonth(); // 0始まり

  function renderCalendar(year, month) {
    $grid.empty();
    const first = new Date(year, month, 1);
    const last = new Date(year, month + 1, 0);

    // 月の最初の週の日曜日から開始
    const start = new Date(first);
    start.setDate(start.getDate() - first.getDay());

    // 5週分描画
    for (let i=0; i<35; i++) {
      const d = new Date(start);
      d.setDate(start.getDate() + i);
      
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, "0");
      const day = String(d.getDate()).padStart(2, "0");
      const ymd = `${y}-${m}-${day}`;
      
      const isOtherMonth = d.getMonth() !== month;
      const isToday = ymd === today;

      const $cell = $(`<div class="day-cell">${d.getDate()}</div>`)
      .attr("data-date", ymd); 
      if (isOtherMonth) $cell.css("color","#bbb");
      if (isToday) $cell.addClass("today");

      $grid.append($cell);
    }

    $(".month-year").text(`${year}年 ${month+1}月`);
  }

  // 初期表示
  renderCalendar(currentYear, currentMonth);

  // 前後移動
  $(".prev-btn").click(() => { renderCalendar(currentYear, --currentMonth); });
  $(".next-btn").click(() => { renderCalendar(currentYear, ++currentMonth); });
  
  // 日付セルクリックで日別画面へ
$(document).on("click", ".day-cell", function(){
  const date = $(this).data("date"); // "yyyy-MM-dd"
  if (date) window.location.href = `/tasks/${date}`; 
  });
  
 // 🔹 ホバーで開くドロップダウン制御
  $('.dropdown').hover(
    function(){
      // マウスがのった時
      $(this).find('.dropdown-menu').addClass('show');
      $(this).find('[data-bs-toggle="dropdown"]').attr('aria-expanded', true);
    },
    function(){
      // マウスが離れた時
      $(this).find('.dropdown-menu').removeClass('show');
      $(this).find('[data-bs-toggle="dropdown"]').attr('aria-expanded', false);
    }
  );
  
  	window.location.href = `/tasks/edit/${taskId}?date=${date}`;
      const selected = document.getElementById('selectedCategory').value;
    if (selected) {
        document.querySelectorAll('.category-item').forEach(item => {
            if (item.dataset.code === selected) {
                item.classList.add('selected');
            }
        });
    }
    
    window.addEventListener('DOMContentLoaded', () => {
    const repeatType = /*[[${task.repeatType}]]*/ 'none';
    const repeatFrequency = /*[[${task.repeatFrequency}]]*/ '';
    const repeatWeekdays = /*[[${task.repeatWeekdays}]]*/ '';
    const repeatMonthDay = /*[[${task.repeatMonthDay}]]*/ '';

    // 繰り返し表示切替
    if (repeatType === 'repeat') {
        document.getElementById('repeatOptions').style.display = 'block';
    }

    // 頻度選択
    document.querySelector('select[name="repeatFrequency"]').value = repeatFrequency;

    if (repeatFrequency === 'weekly') {
        document.getElementById('weeklyOption').style.display = 'block';
        repeatWeekdays.split(',').forEach(day => {
            const checkbox = document.querySelector(`input[name="weekday"][value="${day}"]`);
            if (checkbox) checkbox.checked = true;
        });
    }

    if (repeatFrequency === 'monthly') {
        document.getElementById('monthlyOption').style.display = 'block';
        document.querySelector('input[name="monthDay"]').value = repeatMonthDay || '';
    }
});
});