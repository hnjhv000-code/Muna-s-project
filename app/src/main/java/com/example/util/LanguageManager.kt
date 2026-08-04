package com.example.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val displayName: String, val flagSymbol: String) {
    ARABIC("AR", "العربية", "🇸🇦"),
    ENGLISH("EN", "English", "🇺🇸"),
    FRENCH("FR", "Français", "🇫🇷"),
    JAPANESE("JA", "日本語", "🇯🇵"),
    CHINESE("ZH", "中文", "🇨🇳")
}

object LanguageManager {
    var currentLanguage by mutableStateOf(AppLanguage.ARABIC)

    fun getString(key: String): String {
        val lang = currentLanguage
        val map = translations[key]
        return map?.get(lang) ?: map?.get(AppLanguage.ENGLISH) ?: key
    }

    private val translations = mapOf(
        "app_name" to mapOf(
            AppLanguage.ARABIC to "موقع المنى",
            AppLanguage.ENGLISH to "El-Mona Site",
            AppLanguage.FRENCH to "El-Mona App",
            AppLanguage.JAPANESE to "エル・モナ",
            AppLanguage.CHINESE to "莫纳之光"
        ),
        "subtitle" to mapOf(
            AppLanguage.ARABIC to "تطبيق التركيز وبناء أرض الإنجازات التفاعلية",
            AppLanguage.ENGLISH to "Focus & Interactive Achievements Builder",
            AppLanguage.FRENCH to "Focus & Constructeur d'Accomplissements",
            AppLanguage.JAPANESE to "集中とインタラクティブ実績ランド",
            AppLanguage.CHINESE to "专注与互动成就田园"
        ),
        "tab_apps" to mapOf(
            AppLanguage.ARABIC to "حظر التطبيقات",
            AppLanguage.ENGLISH to "Lock Apps",
            AppLanguage.FRENCH to "Bloquer Apps",
            AppLanguage.JAPANESE to "アプリロック",
            AppLanguage.CHINESE to "应用锁"
        ),
        "tab_focus" to mapOf(
            AppLanguage.ARABIC to "مؤقت التركيز",
            AppLanguage.ENGLISH to "Focus Timer",
            AppLanguage.FRENCH to "Minuteur Focus",
            AppLanguage.JAPANESE to "タイマー",
            AppLanguage.CHINESE to "专注计时"
        ),
        "tab_land" to mapOf(
            AppLanguage.ARABIC to "أرض إنجازاتي",
            AppLanguage.ENGLISH to "My Land",
            AppLanguage.FRENCH to "Mes Succès",
            AppLanguage.JAPANESE to "実績の土地",
            AppLanguage.CHINESE to "我的成就田园"
        ),
        "scan_title" to mapOf(
            AppLanguage.ARABIC to "تطبيقات الهاتف والاذونات",
            AppLanguage.ENGLISH to "Phone Apps & Permissions",
            AppLanguage.FRENCH to "Applications & Permissions",
            AppLanguage.JAPANESE to "アプリと権限",
            AppLanguage.CHINESE to "应用与权限"
        ),
        "scan_desc" to mapOf(
            AppLanguage.ARABIC to "اختر التطبيقات التي تريد إغلاقها أثناء فترة التركيز",
            AppLanguage.ENGLISH to "Select applications to lock during your focus period",
            AppLanguage.FRENCH to "Sélectionnez les applications à verrouiller pendant la session",
            AppLanguage.JAPANESE to "集中時間中にロックするアプリを選択してください",
            AppLanguage.CHINESE to "选择在专注期间需要锁定的应用"
        ),
        "lock_entire_phone" to mapOf(
            AppLanguage.ARABIC to "إغلاق الهاتف كاملاً بشكل عام 🔒",
            AppLanguage.ENGLISH to "Lock Entire Phone (Full Focus) 🔒",
            AppLanguage.FRENCH to "Verrouiller Tout le Téléphone 🔒",
            AppLanguage.JAPANESE to "スマートフォン全体をロック 🔒",
            AppLanguage.CHINESE to "全机锁定 (深度专注) 🔒"
        ),
        "select_all_apps" to mapOf(
            AppLanguage.ARABIC to "تحديد الكل",
            AppLanguage.ENGLISH to "Select All",
            AppLanguage.FRENCH to "Tout Sélectionner",
            AppLanguage.JAPANESE to "すべて選択",
            AppLanguage.CHINESE to "全选"
        ),
        "deselect_all" to mapOf(
            AppLanguage.ARABIC to "إلغاء التحديد",
            AppLanguage.ENGLISH to "Deselect All",
            AppLanguage.FRENCH to "Tout Délectionner",
            AppLanguage.JAPANESE to "選択解除",
            AppLanguage.CHINESE to "取消全选"
        ),
        "selected_apps_count" to mapOf(
            AppLanguage.ARABIC to "التطبيقات المحددة للحظر",
            AppLanguage.ENGLISH to "Selected apps to lock",
            AppLanguage.FRENCH to "Apps sélectionnées pour le verrouillage",
            AppLanguage.JAPANESE to "ロック対象アプリ数",
            AppLanguage.CHINESE to "已选锁定应用"
        ),
        "search_apps" to mapOf(
            AppLanguage.ARABIC to "بحث في التطبيقات...",
            AppLanguage.ENGLISH to "Search apps...",
            AppLanguage.FRENCH to "Rechercher...",
            AppLanguage.JAPANESE to "アプリを検索...",
            AppLanguage.CHINESE to "搜索应用..."
        ),
        "timer_duration" to mapOf(
            AppLanguage.ARABIC to "مدة الإغلاق (حد أقصى 24 ساعة)",
            AppLanguage.ENGLISH to "Lock Duration (Max 24 Hours)",
            AppLanguage.FRENCH to "Durée (Max 24 Heures)",
            AppLanguage.JAPANESE to "時間 (最大24時間)",
            AppLanguage.CHINESE to "锁定时长 (上限24小时)"
        ),
        "select_element" to mapOf(
            AppLanguage.ARABIC to "اختر ما ينمو أثناء التركيز 3D",
            AppLanguage.ENGLISH to "Select What Grows During Focus 3D",
            AppLanguage.FRENCH to "Choisissez ce qui grandit en 3D",
            AppLanguage.JAPANESE to "成長させる要素を選択",
            AppLanguage.CHINESE to "选择专注生长元素 3D"
        ),
        "item_tree" to mapOf(
            AppLanguage.ARABIC to "شجرة مباركة 🌳",
            AppLanguage.ENGLISH to "Blessed Tree 🌳",
            AppLanguage.FRENCH to "Arbre Béni 🌳",
            AppLanguage.JAPANESE to "恵みの木 🌳",
            AppLanguage.CHINESE to "许愿大树 🌳"
        ),
        "item_flower" to mapOf(
            AppLanguage.ARABIC to "زهور وردية 🌸",
            AppLanguage.ENGLISH to "Pink Rose 🌸",
            AppLanguage.FRENCH to "Rose Rose 🌸",
            AppLanguage.JAPANESE to "一輪の花 🌸",
            AppLanguage.CHINESE to "盛开鲜花 🌸"
        ),
        "item_palace" to mapOf(
            AppLanguage.ARABIC to "قصر ذهبي 🏰",
            AppLanguage.ENGLISH to "Golden Palace 🏰",
            AppLanguage.FRENCH to "Palais Doré 🏰",
            AppLanguage.JAPANESE to "黄金の城 🏰",
            AppLanguage.CHINESE to "金色宫殿 🏰"
        ),
        "item_man" to mapOf(
            AppLanguage.ARABIC to "إنسان: رجل 👨",
            AppLanguage.ENGLISH to "Human: Man 👨",
            AppLanguage.FRENCH to "Humain: Homme 👨",
            AppLanguage.JAPANESE to "人物: 男性 👨",
            AppLanguage.CHINESE to "人物: 成年男子 👨"
        ),
        "item_woman" to mapOf(
            AppLanguage.ARABIC to "إنسان: امرأة 👩",
            AppLanguage.ENGLISH to "Human: Woman 👩",
            AppLanguage.FRENCH to "Humain: Femme 👩",
            AppLanguage.JAPANESE to "人物: 女性 👩",
            AppLanguage.CHINESE to "人物: 成年女子 👩"
        ),
        "item_child" to mapOf(
            AppLanguage.ARABIC to "إنسان: طفل 👶",
            AppLanguage.ENGLISH to "Human: Child 👶",
            AppLanguage.FRENCH to "Humain: Enfant 👶",
            AppLanguage.JAPANESE to "人物: 子供 👶",
            AppLanguage.CHINESE to "人物: 孩童 👶"
        ),
        "music_selection" to mapOf(
            AppLanguage.ARABIC to "موسيقى / صوتيات أثناء الإغلاق",
            AppLanguage.ENGLISH to "Background Music & Relaxation Sound",
            AppLanguage.FRENCH to "Musique & Ambiance de Fond",
            AppLanguage.JAPANESE to "バックグラウンドBGM",
            AppLanguage.CHINESE to "专注背景静心音效"
        ),
        "music_none" to mapOf(
            AppLanguage.ARABIC to "بدون صوت (صامت) 🔇",
            AppLanguage.ENGLISH to "No Sound (Silent) 🔇",
            AppLanguage.FRENCH to "Silencieux 🔇",
            AppLanguage.JAPANESE to "消音 🔇",
            AppLanguage.CHINESE to "静音 🔇"
        ),
        "music_rain" to mapOf(
            AppLanguage.ARABIC to "صوت المطر والغابة 🌧️",
            AppLanguage.ENGLISH to "Rain & Forest Birds 🌧️",
            AppLanguage.FRENCH to "Pluie et Forêt 🌧️",
            AppLanguage.JAPANESE to "雨と森の鳥 🌧️",
            AppLanguage.CHINESE to "雨滴与森林 🌧️"
        ),
        "music_melody" to mapOf(
            AppLanguage.ARABIC to "أنغام هادئة للاسترخاء 🎵",
            AppLanguage.ENGLISH to "Soothing Melodies 🎵",
            AppLanguage.FRENCH to "Mélodies Apaisantes 🎵",
            AppLanguage.JAPANESE to "癒しのメロディ 🎵",
            AppLanguage.CHINESE to "舒缓安神旋律 🎵"
        ),
        "music_waves" to mapOf(
            AppLanguage.ARABIC to "أمواج البحر الهادئة 🌊",
            AppLanguage.ENGLISH to "Ocean Waves 🌊",
            AppLanguage.FRENCH to "Vagues de l'Océan 🌊",
            AppLanguage.JAPANESE to "波の音 🌊",
            AppLanguage.CHINESE to "海浪轻抚 🌊"
        ),
        "start_focus" to mapOf(
            AppLanguage.ARABIC to "بدء جلسة التركيز والإغلاق 🚀",
            AppLanguage.ENGLISH to "Start Focus & Lock Session 🚀",
            AppLanguage.FRENCH to "Démarrer la Session Focus 🚀",
            AppLanguage.JAPANESE to "集中セッションを開始 🚀",
            AppLanguage.CHINESE to "开启专注与锁定 🚀"
        ),
        "stop_focus" to mapOf(
            AppLanguage.ARABIC to "مقاطعة / إيقاف الإغلاق 🛑",
            AppLanguage.ENGLISH to "Interrupt & Stop Focus 🛑",
            AppLanguage.FRENCH to "Interrompre la Session 🛑",
            AppLanguage.JAPANESE to "集中を中断 🛑",
            AppLanguage.CHINESE to "中断 / 放弃专注 🛑"
        ),
        "warning_wither" to mapOf(
            AppLanguage.ARABIC to "تنبيه: مقاطعة التركيز ستؤدي إلى انهيار وذبول ما تنميه!",
            AppLanguage.ENGLISH to "Warning: Interrupting focus will crumble and wither your object!",
            AppLanguage.FRENCH to "Attention: Interrompre va flétrir votre création!",
            AppLanguage.JAPANESE to "警告: 中断すると構築中の要素が枯れて崩壊します！",
            AppLanguage.CHINESE to "警告: 中途中断会导致正在生长的建筑或生命枯萎倒塌！"
        ),
        "session_active" to mapOf(
            AppLanguage.ARABIC to "جلسة التركيز جارية...",
            AppLanguage.ENGLISH to "Focus Session Active...",
            AppLanguage.FRENCH to "Session Focus en cours...",
            AppLanguage.JAPANESE to "集中セッション進行中...",
            AppLanguage.CHINESE to "专注 session 进行中..."
        ),
        "time_remaining" to mapOf(
            AppLanguage.ARABIC to "الوقت المتبقي",
            AppLanguage.ENGLISH to "Time Remaining",
            AppLanguage.FRENCH to "Temps Restant",
            AppLanguage.JAPANESE to "残り時間",
            AppLanguage.CHINESE to "剩余时间"
        ),
        "growth_progress" to mapOf(
            AppLanguage.ARABIC to "نسبة النمو والبناء",
            AppLanguage.ENGLISH to "Growth & Construction Progress",
            AppLanguage.FRENCH to "Progrès de Croissance",
            AppLanguage.JAPANESE to "成長・建築進捗率",
            AppLanguage.CHINESE to "生长与建造进度"
        ),
        "land_title" to mapOf(
            AppLanguage.ARABIC to "أرض الإنجازات الزراعية والعائلية 3D",
            AppLanguage.ENGLISH to "Interactive Achievements Estate 3D",
            AppLanguage.FRENCH to "Domaine des Accomplissements 3D",
            AppLanguage.JAPANESE to "インタラクティブ実績ランド 3D",
            AppLanguage.CHINESE to "3D 互动成就家园田园"
        ),
        "land_empty" to mapOf(
            AppLanguage.ARABIC to "أرضك الزراعية واسعة ونقية! أكمل جلسات التركيز لبناء الأشجار، الورود، القصور والعائلات عليها.",
            AppLanguage.ENGLISH to "Your land is vast and peaceful! Complete focus sessions to grow trees, flowers, palaces, and families.",
            AppLanguage.FRENCH to "Votre terre est vaste! Complétez des sessions focus pour y construire des arbres, palais et familles.",
            AppLanguage.JAPANESE to "あなたの広大な土地です！集中タイマーを完了して木、花、城、家族を建てましょう。",
            AppLanguage.CHINESE to "广阔而美丽的田野！完成专注目标，种下大树、鲜花、城堡与温馨家庭。"
        ),
        "family_notice" to mapOf(
            AppLanguage.ARABIC to "👨‍👩‍👧 نظام العائلة: عند بناء رجل ثم امرأة ثم طفل، يصبح الطفل طفلهما، ويتم الاعتناء به ويكبر أمامك!",
            AppLanguage.ENGLISH to "👨‍👩‍👧 Family System: Creating a Man, Woman, then Child forms a family! Tapping other humans matures children!",
            AppLanguage.FRENCH to "👨‍👩‍👧 Système de Famille: Homme, Femme et Enfant forment une famille qui grandit!",
            AppLanguage.JAPANESE to "👨‍👩‍👧 家族システム: 男性、女性、子供の順に作成すると家族になり、成長していきます！",
            AppLanguage.CHINESE to "👨‍👩‍👧 家庭成长机制: 建造男子、女子与孩童将组合为幸福家庭，并在后续阶段茁壮成长！"
        ),
        "clear_land" to mapOf(
            AppLanguage.ARABIC to "مسح الأرض",
            AppLanguage.ENGLISH to "Reset Land",
            AppLanguage.FRENCH to "Réinitialiser",
            AppLanguage.JAPANESE to "リセット",
            AppLanguage.CHINESE to "重置家园"
        ),
        "language_dialog_title" to mapOf(
            AppLanguage.ARABIC to "اختر لغة التطبيق / Select Language",
            AppLanguage.ENGLISH to "Select Application Language",
            AppLanguage.FRENCH to "Choisir la Langue",
            AppLanguage.JAPANESE to "言語を選択",
            AppLanguage.CHINESE to "选择应用语言"
        ),
        "theme_light" to mapOf(
            AppLanguage.ARABIC to "الوضع الساطع ☀️",
            AppLanguage.ENGLISH to "Day Light Mode ☀️",
            AppLanguage.FRENCH to "Mode Jour ☀️",
            AppLanguage.JAPANESE to "ライトモード ☀️",
            AppLanguage.CHINESE to "日间明亮模式 ☀️"
        ),
        "theme_dark" to mapOf(
            AppLanguage.ARABIC to "الوضع الليلي النيون 🌙",
            AppLanguage.ENGLISH to "Night Neon Mode 🌙",
            AppLanguage.FRENCH to "Mode Nuit Néon 🌙",
            AppLanguage.JAPANESE to "ナイトネオンモード 🌙",
            AppLanguage.CHINESE to "夜间霓虹模式 🌙"
        ),
        "congrats" to mapOf(
            AppLanguage.ARABIC to "تهانينا! اكتملت جلسة التركيز بنجاح! 🎉",
            AppLanguage.ENGLISH to "Congratulations! Focus session completed! 🎉",
            AppLanguage.FRENCH to "Félicitations! Session réussie! 🎉",
            AppLanguage.JAPANESE to "おめでとうございます！集中完了！ 🎉",
            AppLanguage.CHINESE to "恭喜！专注时间顺利达成！ 🎉"
        ),
        "added_to_land" to mapOf(
            AppLanguage.ARABIC to "تمت إضافة إنجازك إلى أرض الإنجازات الواسعة!",
            AppLanguage.ENGLISH to "Your masterpiece has been added to your vast land!",
            AppLanguage.FRENCH to "Votre chef-d'œuvre a été ajouté à votre terre!",
            AppLanguage.JAPANESE to "あなたの作品が広大な土地に追加されました！",
            AppLanguage.CHINESE to "你的成果已放置在辽阔的成就田园中！"
        ),
        "hours" to mapOf(
            AppLanguage.ARABIC to "ساعة",
            AppLanguage.ENGLISH to "Hours",
            AppLanguage.FRENCH to "Heures",
            AppLanguage.JAPANESE to "時間",
            AppLanguage.CHINESE to "小时"
        ),
        "minutes" to mapOf(
            AppLanguage.ARABIC to "دقيقة",
            AppLanguage.ENGLISH to "Minutes",
            AppLanguage.FRENCH to "Minutes",
            AppLanguage.JAPANESE to "分",
            AppLanguage.CHINESE to "分钟"
        ),
        "seconds" to mapOf(
            AppLanguage.ARABIC to "ثانية",
            AppLanguage.ENGLISH to "Seconds",
            AppLanguage.FRENCH to "Secondes",
            AppLanguage.JAPANESE to "秒",
            AppLanguage.CHINESE to "秒"
        ),
        "strict_mode" to mapOf(
            AppLanguage.ARABIC to "وضع الإغلاق الصارم",
            AppLanguage.ENGLISH to "Strict Lock Mode",
            AppLanguage.FRENCH to "Mode Verrouillage Stricte",
            AppLanguage.JAPANESE to "厳格ロックモード",
            AppLanguage.CHINESE to "严格锁定模式"
        ),
        "strict_mode_desc" to mapOf(
            AppLanguage.ARABIC to "عند التفعيل لن تتمكن من إلغاء أو إيقاف المؤقت بأي شكل حتى انتهاء العد التنازلي",
            AppLanguage.ENGLISH to "When enabled, you cannot cancel or stop the timer until countdown completes",
            AppLanguage.FRENCH to "Empêche l'annulation ou l'arrêt du minuteur avant la fin",
            AppLanguage.JAPANESE to "有効にするとタイマー終了までキャンセル・停止できません",
            AppLanguage.CHINESE to "开启后在倒计时结束前无法取消或停止计时"
        ),
        "strict_mode_active_warning" to mapOf(
            AppLanguage.ARABIC to "🔒 وضع الإغلاق الصارم مفعل! لا يمكنك إيقاف المؤقت حتى انتهاء الوقت.",
            AppLanguage.ENGLISH to "🔒 Strict Lock Mode is active! You cannot stop the session early.",
            AppLanguage.FRENCH to "🔒 Mode Stricte actif! Impossible d'arrêter la session.",
            AppLanguage.JAPANESE to "🔒 厳格ロック中！途中停止はできません。",
            AppLanguage.CHINESE to "🔒 严格锁定中！无法提前停止。"
        ),
        "item_details_title" to mapOf(
            AppLanguage.ARABIC to "تفاصيل الإنجاز",
            AppLanguage.ENGLISH to "Achievement Details",
            AppLanguage.FRENCH to "Détails de l'Accomplissement",
            AppLanguage.JAPANESE to "実績の詳細",
            AppLanguage.CHINESE to "成就详情"
        ),
        "date_created" to mapOf(
            AppLanguage.ARABIC to "تاريخ الإنجاز",
            AppLanguage.ENGLISH to "Achievement Date",
            AppLanguage.FRENCH to "Date d'Accomplissement",
            AppLanguage.JAPANESE to "達成日時",
            AppLanguage.CHINESE to "完成时间"
        ),
        "focus_duration" to mapOf(
            AppLanguage.ARABIC to "مدة التركيز",
            AppLanguage.ENGLISH to "Focus Duration",
            AppLanguage.FRENCH to "Durée de Focus",
            AppLanguage.JAPANESE to "集中時間",
            AppLanguage.CHINESE to "专注时长"
        ),
        "close" to mapOf(
            AppLanguage.ARABIC to "إغلاق",
            AppLanguage.ENGLISH to "Close",
            AppLanguage.FRENCH to "Fermer",
            AppLanguage.JAPANESE to "閉じる",
            AppLanguage.CHINESE to "关闭"
        ),
        "zoom_hint" to mapOf(
            AppLanguage.ARABIC to "💡 استخدم إصبعيك للتكبير والتصغير أو اسحب للتحرك في أرضك",
            AppLanguage.ENGLISH to "💡 Pinch to zoom in/out or drag to explore your land",
            AppLanguage.FRENCH to "💡 Pincez pour zoomer et faites glisser pour explorer",
            AppLanguage.JAPANESE to "💡 ピンチで拡大縮小、ドラッグで移動できます",
            AppLanguage.CHINESE to "💡 双指缩放，拖动可探索广阔田园"
        ),
        "music_custom" to mapOf(
            AppLanguage.ARABIC to "موسيقى من الهاتف 🎵",
            AppLanguage.ENGLISH to "Phone Music 🎵",
            AppLanguage.FRENCH to "Musique du Téléphone 🎵",
            AppLanguage.JAPANESE to "スマホの音楽 🎵",
            AppLanguage.CHINESE to "本地手机音乐 🎵"
        ),
        "select_custom_audio" to mapOf(
            AppLanguage.ARABIC to "📂 اختر ملف صوتي من الهاتف",
            AppLanguage.ENGLISH to "📂 Select Audio File from Phone",
            AppLanguage.FRENCH to "📂 Choisir un fichier audio",
            AppLanguage.JAPANESE to "📂 音声ファイルを選択",
            AppLanguage.CHINESE to "📂 选择本地音频文件"
        ),
        "custom_audio_selected" to mapOf(
            AppLanguage.ARABIC to "🎵 الملف المحدد: ",
            AppLanguage.ENGLISH to "🎵 Selected File: ",
            AppLanguage.FRENCH to "🎵 Fichier sélectionné: ",
            AppLanguage.JAPANESE to "🎵 選択されたファイル: ",
            AppLanguage.CHINESE to "🎵 已选文件: "
        ),
        "add_custom_audio" to mapOf(
            AppLanguage.ARABIC to "➕ إضافة ملفات صوتية",
            AppLanguage.ENGLISH to "➕ Add Audio Files",
            AppLanguage.FRENCH to "➕ Ajouter des fichiers audio",
            AppLanguage.JAPANESE to "➕ 音声ファイルを追加",
            AppLanguage.CHINESE to "➕ 添加音频文件"
        ),
        "clear_playlist" to mapOf(
            AppLanguage.ARABIC to "مسح الكل",
            AppLanguage.ENGLISH to "Clear All",
            AppLanguage.FRENCH to "Tout effacer",
            AppLanguage.JAPANESE to "Tout effacer",
            AppLanguage.CHINESE to "清空列表"
        ),
        "playlist_count" to mapOf(
            AppLanguage.ARABIC to "🎵 قائمة التشغيل (%d ملفات - تعمل بالترتيب وتتكرر):",
            AppLanguage.ENGLISH to "🎵 Playlist (%d files - sequence playback):",
            AppLanguage.FRENCH to "🎵 Liste de lecture (%d fichiers - lecture séquentielle):",
            AppLanguage.JAPANESE to "🎵 プレイリスト (%d ファイル - 順番に再生):",
            AppLanguage.CHINESE to "🎵 播放列表 (%d 个文件 - 顺序循环播放):"
        ),
        "single_file_loop_hint" to mapOf(
            AppLanguage.ARABIC to "🔄 سيتم تكرار الملف طوال فترة التوقيف",
            AppLanguage.ENGLISH to "🔄 Single file will loop during focus session",
            AppLanguage.FRENCH to "🔄 Le fichier unique sera répété en boucle",
            AppLanguage.JAPANESE to "🔄 単一ファイルがループ再生されます",
            AppLanguage.CHINESE to "🔄 单个文件将在专注期间循环播放"
        )
    )
}
