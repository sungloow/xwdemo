<template>
  <div class="time-box">
    <div class="clock-box">
      <canvas ref="canvas" height="80" width="80"></canvas>
    </div>
    <div class="time-date-box">
      <div>
        <span class="time">{{ time.time }}</span>
        <span class="second">{{ time.second }}</span>
      </div>
      <div>
        <span class="week">{{ time.week }}</span>
        <span class="date">{{ time.date }}</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted, onBeforeUnmount } from 'vue';
  import dayjs from 'dayjs';

  const timer: any = ref(null);
  const time: any = ref({});

  const getDate = () => {
    time.value = parseTime(new Date());
  };
  // 根据传入的时间，处理成需要的对象函数
  const parseTime = (time: any) => {
    draw(time);
    let resObj: any = {};
    let week = ['日', '一', '二', '三', '四', '五', '六'];
    resObj.date = dayjs(time).format('YYYY年MM月DD日');
    resObj.week = `星期${week[dayjs(time).day()]}`;
    resObj.time = dayjs(time).format('HH:mm');
    resObj.second = dayjs(time).format('ss');
    return resObj;
  };
  const canvas = ref();
  //描绘时分秒小数和小数点
  // 绘制表盘
  const radius = 40;
  const cWidth = 80;
  const drawBackground = () => {
    ctx.value.save();
    // ctx.value.translate(radius, radius); //中心原点位置
    const width = cWidth;
    const height = cWidth;
    const R = width / 2; //圆半径
    const r = (R / 3) * 2;
    //设置坐标轴原点
    ctx.value.translate(width / 2, height / 2);
    // 圆心
    ctx.value.beginPath();
    // ctx.value.arc(0, 0, 10, 0, 2 * Math.PI, true);
    ctx.value.fill();
    ctx.value.closePath();
    // 表盘刻度（大格）
    ctx.value.beginPath();
    ctx.value.lineWidth = 2;
    for (let i = 0; i < 12; i++) {
      ctx.value.beginPath();
      ctx.value.strokeStyle = 'rgba(255,255,255,1)';
      ctx.value.rotate(Math.PI / 6);
      ctx.value.moveTo(R, 0);
      ctx.value.lineTo(r, 0);
      ctx.value.stroke();
    }
    ctx.value.closePath();
    // 表盘刻度（小格）
    ctx.value.beginPath();
    ctx.value.lineWidth = 1;
    for (let i = 0; i < 60; i++) {
      ctx.value.beginPath();
      ctx.value.strokeStyle = 'rgba(255,255,255,0.7)';
      ctx.value.rotate(Math.PI / 30);
      ctx.value.moveTo(R, 0);
      ctx.value.lineTo(R - 10, 0);
      ctx.value.stroke();
    }
    ctx.value.closePath();
  };

  //描绘时针
  const drawHour = (hour: any, minute: any) => {
    ctx.value.save();
    ctx.value.beginPath();
    ctx.value.strokeStyle = '#FFFFFF';
    let rad = ((2 * Math.PI) / 12) * hour;
    let mrad = ((2 * Math.PI) / 12 / 60) * minute;
    ctx.value.rotate(rad + mrad);
    ctx.value.lineWidth = 4;
    ctx.value.lineCap = 'round';
    ctx.value.moveTo(0, 10);
    ctx.value.lineTo(0, -radius + 30);
    ctx.value.stroke();
    ctx.value.restore();
  };
  //描绘分针
  const drawMinute = (minute: any) => {
    ctx.value.save();
    ctx.value.beginPath();
    let rad = ((2 * Math.PI) / 60) * minute;
    ctx.value.rotate(rad);
    ctx.value.strokeStyle = '#FFFFFF';
    ctx.value.lineWidth = 3;
    ctx.value.lineCap = 'round';
    ctx.value.moveTo(0, 10);
    ctx.value.lineTo(0, -radius + 20);
    ctx.value.stroke();
    ctx.value.restore();
  };
  //描绘秒针
  const drawSecond = (second: any) => {
    ctx.value.save();
    ctx.value.beginPath();
    ctx.value.fillStyle = '#C14543';
    ctx.value.lineWidth = 2;
    ctx.value.lineCap = 'round';
    let rad = ((2 * Math.PI) / 60) * second;
    ctx.value.rotate(rad);
    ctx.value.moveTo(-2, 20);
    ctx.value.lineTo(2, 20);
    ctx.value.lineTo(1, -radius + 10);
    ctx.value.lineTo(-1, -radius + 10);
    ctx.value.fill();
    ctx.value.restore();
  };
  //中间固定园点
  const drawDot = () => {
    ctx.value.beginPath();
    ctx.value.fillStyle = '#C14543';
    ctx.value.arc(0, 0, 4, 0, 2 * Math.PI, false);
    ctx.value.fill();
  };
  const draw = (now: any) => {
    ctx.value.clearRect(0, 0, cWidth, cWidth);
    let hour = now.getHours();
    let minute = now.getMinutes();
    let second = now.getSeconds();
    drawBackground();
    drawHour(hour, minute);
    drawMinute(minute);
    drawSecond(second);
    drawDot();
    ctx.value.restore();
  };

  const ctx = ref();
  onMounted(() => {
    ctx.value = canvas.value.getContext('2d');
    timer.value = setInterval(() => {
      getDate();
    }, 1000);
  });
  onBeforeUnmount(() => {
    clearInterval(timer.value);
    timer.value = null;
  });
</script>
<style lang="scss" scoped>
  .time-box {
    display: flex;
    padding: 10px 0;
    justify-content: center;
    border-radius: 4px;
    margin: 0 auto;
    background: linear-gradient(to right bottom, #7cafe3, #046ac1);
    cursor: default;
    text-align: center;

    .clock-box {
      width: 140px;
      margin: auto;
    }

    .time-date-box {
      flex: 1;
      margin: auto;

      color: #fff;


      .time {
        line-height: 50px;
        font-size: 32px;
        letter-spacing: 5px;
      }

      .second {
        font-size: 18px;
        margin-left: 10px;
      }

      .week {
        line-height: 30px;
        font-size: 18px;
        letter-spacing: 2px;
      }

      .date {
        font-size: 14px;
        margin-left: 30px;
        letter-spacing: 2px;
      }
    }

  }
</style>
