<template>

<div class="container">
    <el-container>
      <el-header class="header" height="12vh">
   <div class="mt-4">
    <el-input
      v-model="input3"
      placeholder="请输入查询内容"
      class="input-with-select"
      clearable
      size=large
    >
      <template #append>
        <el-button plain  :icon="Search" @click="searchFor" primary/>
      </template>
    </el-input>
  </div>
      </el-header>


      <el-main>

        <el-scrollbar height="526px">
       
          <div style="white-space: pre-wrap;" v-html="brightenKeyword(info,input3)"></div>
    <!-- <div>{{info}}
     <span style="color:red">333 </span>
    </div> -->
  </el-scrollbar>
  

      </el-main>
      <el-footer class="common-layout" height="10vh">

        <div class="demo-pagination-block">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[1]"
      :small="small"
      :disabled="disabled"
      :background="background"
      layout="total, sizes, prev, pager, next, jumper"
      v-model:total="pageTotal"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>

      </el-footer>
    </el-container>
  </div>

   
</template>
<script setup>
import { ref } from 'vue'
import request from '../utils/Request.js'
import { Search } from '@element-plus/icons-vue'
const input3 = ref("")
let info = ref("")

let currentPage = ref(1)
let pageSize = ref(1)
let pageTotal = ref(0)
const small = ref(false)
const background = ref(false)
const disabled = ref(false)

let searchFor = ()=>{
  let params={
    search:input3.value,
    pageNo:currentPage.value,
    pageSize:pageSize.value,
  }
  console.log(params.pageNo);
  request.post("/search/queryList",params).then(res=>{
    pageTotal.value = res.total
    info.value = res.vki[0].text
    console.log(res);
    brightenKeyword(info.value,input3);
  })
}

  //搜索高亮
 const brightenKeyword = (val,keyword) =>{
       // let keywordArr = keyword.split("");
       var str = val
       console.log(val);
    val = str.replace("<font style=\"color:red\">",'<font style=\"color:red\">')
    val = val.replace("</font>",'</font>')  
    console.log(val);
        val = val + "";
        return val;   
    }

const handleSizeChange = (val) => {
  console.log(`${val} items per page`)
}
const  handleCurrentChange = async (val) => {
  console.log(`current page: ${val}`)
  currentPage.val = val;//改变当前的页码值
  searchFor();//调用向后端发送新的页码需要的数据
}


</script>
<style lang = "scss">

.container{
  height:100%;
}
.header{
  border-style: none none solid none;
  border-bottom-color: rgb(232, 235, 240);
}
.common-layout{
    height:18vh;
    background-color: rgb(232, 235, 240);
}
.mt-4{
    width:50%;
    margin-top: 24px;
    margin-left: 300px;
}
.input-with-select .el-input-group__prepend {
  background-color: var(--el-fill-color-blank);
}

.text {
  font-size: 14px;
}

.item {
  padding: 18px 0;
}
.demo-pagination-block{
  padding-top: 2vh;
  padding-left: 45vh;
}
.box-card {
  width: 480px;
}
</style>