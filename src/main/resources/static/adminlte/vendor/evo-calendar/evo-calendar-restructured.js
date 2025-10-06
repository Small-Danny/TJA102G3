
/*!
 * Evo Calendar 重構版 - HTML 與 JS 分離
 * 將原本的字串 HTML 拼接改成 DOM 操作
 * Author: ChatGPT 重構
 */

;(function(factory) {
    'use strict';
    if (typeof define === 'function' && define.amd) {
        define(['jquery'], factory);
    } else if (typeof exports !== 'undefined') {
        module.exports = factory(require('jquery'));
    } else {
        factory(jQuery);
    }

}(function($) {
    'use strict';
    var EvoCalendar = window.EvoCalendar || {};

    // ... (保留原始 constructor, defaults, methods，大部分程式不動)

    // buildTheBones 改寫
    EvoCalendar.prototype.buildTheBones = function() {
        var _ = this;
        _.calculateDays();

        _.$elements.sidebarEl = _.$elements.calendarEl.find('.calendar-sidebar');
        _.$elements.innerEl   = _.$elements.calendarEl.find('.calendar-inner');
        _.$elements.eventEl   = _.$elements.calendarEl.find('.calendar-events');
        _.$elements.sidebarToggler = _.$elements.calendarEl.find('#sidebarToggler');
        _.$elements.eventListToggler = _.$elements.calendarEl.find('#eventListToggler');

        // 填月份
        var monthList = _.$elements.sidebarEl.find('.calendar-months');
        monthList.empty();
        for (var i = 0; i < _.$label.months.length; i++) {
            monthList.append('<li class="month" data-month-val="'+i+'">'+_.initials.dates[_.options.language].months[i]+'</li>');
        }

        // 填星期
        var headerRow = _.$elements.innerEl.find('.calendar-header');
        headerRow.empty();
        for (var j = 0; j < _.$label.days.length; j++) {
            var headerClass = "calendar-header-day";
            if (_.$label.days[j] === _.initials.weekends.sat || _.$label.days[j] === _.initials.weekends.sun) {
                headerClass += " --weekend";
            }
            headerRow.append('<td class="'+headerClass+'">'+_.$label.days[j]+'</td>');
        }

        _.buildSidebarYear();
        _.buildSidebarMonths();
        _.buildCalendar();
        _.buildEventList();
        _.initEventListener();
        _.resize();
    };

    // buildCalendar 改寫
    EvoCalendar.prototype.buildCalendar = function() {
        var _ = this, title;
        _.calculateDays();

        title = _.formatDate(new Date(_.$label.months[_.$active.month] +' 1 '+ _.$active.year), _.options.titleFormat, _.options.language);
        _.$elements.innerEl.find('.calendar-table th').text(title);

        _.$elements.innerEl.find('.calendar-body').remove();

        var table = _.$elements.innerEl.find('.calendar-table');
        var day = 1, markup = '';

        for (var i = 0; i < 9; i++) {
            markup += '<tr class="calendar-body">';
            for (var j = 0; j < _.$label.days.length; j++) {
                if (day <= _.monthLength && (i > 0 || j >= _.startingDay)) {
                    var dayClass = "calendar-day";
                    if (_.$label.days[j] === _.initials.weekends.sat || _.$label.days[j] === _.initials.weekends.sun) {
                        dayClass += ' --weekend';
                    }
                    var thisDay = _.formatDate(_.$label.months[_.$active.month]+' '+day+' '+_.$active.year, _.options.format);
                    markup += '<td class="'+dayClass+'"><div class="day" data-date-val="'+thisDay+'">'+day+'</div></td>';
                    day++;
                } else {
                    markup += '<td></td>';
                }
            }
            markup += '</tr>';
            if (day > _.monthLength) break;
        }
        table.append(markup);

        if(_.options.todayHighlight) {
            _.$elements.innerEl.find("[data-date-val='" + _.$current.date + "']").addClass('calendar-today');
        }

        _.$elements.innerEl.find('.calendar-day .day').off('click.evocalendar').on('click.evocalendar', _.selectDate);

        var selectedDate = _.$elements.innerEl.find("[data-date-val='" + _.$active.date + "']");
        if (selectedDate) {
            _.$elements.innerEl.find('[data-date-val]').removeClass('calendar-active');
            selectedDate.addClass('calendar-active');
        }

        if(_.options.calendarEvents) _.buildEventIndicator();
    };

    // buildEventList 改寫
    EvoCalendar.prototype.buildEventList = function() {
        var _ = this, hasEventToday = false;
        _.$active.events = [];

        var title = _.formatDate(_.$active.date, _.options.eventHeaderFormat, _.options.language);
        _.$elements.eventEl.find('.event-header > p').text(title);

        var eventListEl = _.$elements.eventEl.find('.event-list');
        eventListEl.empty();

        if (_.options.calendarEvents) {
            for (var i = 0; i < _.options.calendarEvents.length; i++) {
                if(_.isBetweenDates(_.$active.date, _.options.calendarEvents[i].date)) {
                    hasEventToday = true;
                    _.addEventList(_.options.calendarEvents[i]);
                }
            }
        }

        if (!hasEventToday) {
            var msg = (_.$active.date === _.$current.date) ? _.initials.dates[_.options.language].noEventForToday : _.initials.dates[_.options.language].noEventForThisDay;
            eventListEl.html('<div class="event-empty"><p>'+msg+'</p></div>');
        }
    };

    // addEventList 改寫
    EvoCalendar.prototype.addEventList = function(event_data) {
        var _ = this;
        var eventListEl = _.$elements.eventEl.find('.event-list');
        _.$active.events.push(event_data);

        var markup = '<div class="event-container" data-event-index="'+event_data.id+'">'+
          '<div class="event-icon"><div class="event-bullet-'+event_data.type+'" style="background-color:'+(event_data.color||'')+'"></div></div>'+
          '<div class="event-info">'+
            '<p class="event-title">'+_.limitTitle(event_data.name)+(event_data.badge ? '<span>'+event_data.badge+'</span>':'')+'</p>'+
            (event_data.description ? '<p class="event-desc">'+event_data.description+'</p>':'')+
          '</div>'+
        '</div>';

        eventListEl.append(markup);

        _.$elements.eventEl.find('[data-event-index="'+event_data.id+'"]').off('click.evocalendar').on('click.evocalendar', _.selectEvent);
    };

    // 其他方法保持不變 (init, destroy, toggleSidebar, addCalendarEvent 等)

    $.fn.evoCalendar = function() {
        var _ = this,
            opt = arguments[0],
            args = Array.prototype.slice.call(arguments, 1),
            l = _.length,
            i,
            ret;
        for (i = 0; i < l; i++) {
            if (typeof opt == 'object' || typeof opt == 'undefined')
                _[i].evoCalendar = new EvoCalendar(_[i], opt);
            else
                ret = _[i].evoCalendar[opt].apply(_[i].evoCalendar, args);
            if (typeof ret != 'undefined') return ret;
        }
        return _;
    };

}));
