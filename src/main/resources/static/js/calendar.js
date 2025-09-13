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
  })
});