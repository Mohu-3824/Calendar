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
        	markTaskDays(validDates);               // タスク有日ハイライト
        	fetchCompletedTaskTitles(validDates);   // 達成済タスク一覧取得＆表示
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
    
    /* ======================
       🏆 達成済みタスク名取得
    ====================== */
    function fetchCompletedTaskTitles(dateList) {
        if (!Array.isArray(dateList) || dateList.length === 0) return;

        const csrfToken = $('meta[name="_csrf"]').attr('content');
        const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
        

        $.ajax({
            url: "/calendar/completed-tasktitles",
            method: "GET",
            data: { dates: dateList },
            traditional: true,
            headers: csrfToken && csrfHeader ? { [csrfHeader]: csrfToken } : {},
            success: function (completedMap) {
                // completedMap = { "2025-09-02": ["掃除", "勉強"], ... }
                for (const dateStr in completedMap) {
                    const $cell = $(`.day-cell[data-date="${dateStr}"]`);
                    $cell.addClass("has-task");
                    
                    // 既存の.task-listを一旦削除
        			$cell.find(".task-list").remove();
        			
                    const tasks = completedMap[dateStr];
                	if (tasks.length > 0) {
						const taskListHtml = tasks.map(t => {
                        	// アイコンがある場合のみ画像タグを用意
                        	let iconHtml = "";
                        	if (t.icon) {
                            	iconHtml = `<img src="/img/categoryImage/${t.icon}" 
                                              alt="category icon" 
                                              class="task-icon"
                                              style="width:16px;height:16px;margin-right:4px;">`;
                        	}
                        	return `<div class="task-title" style="background-color:${t.color}">
                                    	${iconHtml}${t.title}
                                	</div>`;
                    	}).join("");

                    	$cell.append(`<div class="task-list">${taskListHtml}</div>`);
                	}
                }
            },
            error: function () {
                console.error("達成済みタスク取得エラー");
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
        date = String(date || '').replace(/"/g, '');

    	const csrfToken = $('meta[name="_csrf"]').attr('content');
    	const csrfHeaderName = $('meta[name="_csrf_header"]').attr('content');

   		$.ajax({
      		url: "/tasks/toggle",
      		type: "POST",
      		data: { taskId: taskId, done: isChecked, date: date },
      		headers: csrfToken && csrfHeaderName ? { [csrfHeaderName]: csrfToken } : {}
    	})
    	.done(function () {
      		if (isChecked) {
        		showFooterMessage(`${title} を達成済に移動しました。`);
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
		
	// ======================
	// 🔄 タスク新規作成・編集画面：カテゴリー選択処理
	// ======================
	const hiddenField = document.getElementById("categoryId");
	if (hiddenField) {
  		const items = document.querySelectorAll(".category-item");
  		items.forEach(item => {
    		item.addEventListener("click", function () {
      		const categoryId = this.getAttribute("data-category-id");
      		hiddenField.value = categoryId;
      		items.forEach(el => el.classList.remove("selected"));
      		this.classList.add("selected");
    	});
  	});

  	const currentCategoryId = hiddenField.value;
  	if (currentCategoryId) {
    	const selectedItem = document.querySelector(
      `	.category-item[data-category-id="${currentCategoryId}"]`
    	);
    	if (selectedItem) selectedItem.classList.add("selected");
  	}
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