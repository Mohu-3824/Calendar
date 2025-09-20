$(function() {
    /* ======================
       📅 カレンダー描画部分
    ====================== */	
  const $grid = $("#calendarGrid");
  const today = $grid.data("today"); // "2025-09-10"
  const now = new Date(today);

  let currentYear = now.getFullYear();
  let currentMonth = now.getMonth(); // 0始まり

  function renderCalendar(year, month) {
    $grid.empty();
    let dates = [];
    
    const first = new Date(year, month, 1);
    const last = new Date(year, month + 1, 0);

    // 月の最初の週の日曜日から開始
    const start = new Date(first);
    start.setDate(start.getDate() - first.getDay());

    // 5週分描画
    for (let i=0; i<35; i++) {
      const d = new Date(start);
      d.setDate(start.getDate() + i);
      
      // NaN防止
      if (isNaN(d.getTime())) {
		  console.warn("Invalid Date detected - skipped:", d);
		  continue;
	  }  
	      
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, "0");
      const day = String(d.getDate()).padStart(2, "0");
      const ymd = `${y}-${m}-${day}`;
      dates.push(ymd); // 後で「タスクありチェック」に使う
      
      const isOtherMonth = d.getMonth() !== month;
      const isToday = ymd === today;

      const $cell = $(`<div class="day-cell">${d.getDate()}</div>`)
      .attr("data-date", ymd); 
      if (isOtherMonth) $cell.css("color","#bbb");
      if (isToday) $cell.addClass("today");

      $grid.append($cell);
    }

    $(".month-year").text(`${year}年 ${month+1}月`);
    
    // ✅ API送信前に日付形式をチェック（YYYY-MM-DDのみ通す）
    const validDates = dates.filter(dateStr => /^\d{4}-\d{2}-\d{2}$/.test(dateStr));
    	if (validDates.length > 0) {
        	markTaskDays(validDates);
        }
    } 

    /* ======================
        🔍 APIでタスク有日をハイライト
    ====================== */
    function markTaskDays(dateList) {
		if (!Array.isArray(dateList) || dateList.length === 0) {
        	return; // 空なら送信しない
    	}
        $.ajax({
            url: "/calendar/taskdays", // タスク有日取得用API
            method: "GET",
            data: { dates: dateList }, // クエリパラメータで送信
            traditional: true, // 配列をdates=...&dates=...形式で送る
            success: function(taskDays) {
                // taskDays = ["2025-09-02", "2025-09-14", ...]
                taskDays.forEach(dateStr => {
                    $(`.day-cell[data-date="${dateStr}"]`).addClass("has-task");
                });
            },
            error: function() {
                console.error("タスク予定取得エラー");
            }
        });
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

    /* ======================
       📌 ドロップダウン ホバー表示
    ====================== */  
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

    /* ======================
       ✅ タスク完了トグル（リロードなし版）
    ====================== */  
    window.toggleTaskCompletion = function (taskId, isChecked, date, title) {
        date = date.replace(/"/g, ''); // クオート除去

        $.post("/tasks/toggle", { taskId: taskId, done: isChecked, date: date })
            .done(function () {
                if (isChecked) {
                    // メッセージ表示
                    showFooterMessage(`${title} を達成済に移動しました。`);
                    // DOMを未達成リストから達成済リストへ移動
                    moveTaskBetweenLists(taskId, true);
                } else {
                    showFooterMessage(`${title} を未達成に戻しました。`);
                    moveTaskBetweenLists(taskId, false);
                }
            })
            .fail(function () {
                alert("エラーが発生しました");
            });
    };
 
     /* ======================
       🔄 タスクDOMを移動させる関数
    ====================== */
    function moveTaskBetweenLists(taskId, toCompleted) {
        const task = $(`input[type="checkbox"][value="${taskId}"]`).closest(".task-item");
        if (toCompleted) {
            $("h5:contains('★達成済')").nextAll(".task-item, .text-muted").first().before(task);
        } else {
            $("h5:contains('☆未達成')").nextAll(".task-item, .text-muted").first().before(task);
        }
        // チェックボックス状態を即反映
        task.find('input[type="checkbox"]').prop('checked', toCompleted);
    }
    
  	window.location.href = `/tasks/edit/${taskId}?date=${date}`;
      const selected = document.getElementById('selectedCategory').value;
    if (selected) {
        document.querySelectorAll('.category-item').forEach(item => {
            if (item.dataset.code === selected) {
                item.classList.add('selected');
            }
        });
    }
    
    /* ======================
       💬 下部メッセージ表示
    ====================== */
    function showFooterMessage(message) {
        let msgDiv = document.createElement("div");
        msgDiv.textContent = message;
        msgDiv.className = "footer-message";
        document.body.appendChild(msgDiv);

        setTimeout(() => { msgDiv.classList.add("fade-out"); }, 2000);
        setTimeout(() => { msgDiv.remove(); }, 3000);
    }
});